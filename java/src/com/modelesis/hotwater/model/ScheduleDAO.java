/**
 * 
 */
package com.modelesis.hotwater.model;

/**
 * The ScheduleDAO class serializes schedule
 * data into a format that the HotWater
 * hardware can understand and manage.
 * 
 * @author ramon
 */
public class ScheduleDAO {
	
	/**
	 * Serializes the schedule.  Serialization
	 * turns the schedule into a byte array,
	 * with one byte for each hour in a week (168 total);
	 * within each byte, the six least significant bits
	 * are used to represent the state of the hour's six
	 * 10-digit segments.  The least significant bit is
	 * the :00 to :09 segment, the next bit is :10 to :19, etc.
	 * 
	 * @param sched Schedule object to serialize
	 * @return Serialized version of schedule object
	 */
	public byte[] serializeScheduleToHardware(Schedule sched) {
		byte[] serSched = new byte[24 * 7];  // Hours per week
		byte currentHour = 0;

		for(int i = Schedule.MIN_WEEKDAY; i <= Schedule.MAX_WEEKDAY; i++)
			for(int j = Schedule.MIN_SEGMENT; j <= Schedule.MAX_SEGMENT; j++) {
				int hourOfDay = j / 6;
				int segment = j % 6;
				if(segment == 0)
					currentHour = 0;
				boolean status = sched.get(i, j);
				int bit = status ? 1 : 0;
				currentHour |= (bit << segment); 
				if(segment == 5)
					serSched[24 * i + hourOfDay] = currentHour;
			}
		
		return serSched;
	}
	
	/**
	 * Serializes the schedule into a string variable.
	 * This simply produces a string representation of
	 * the byte array used to synchronize the HotWater device.
	 * 
	 * @param sched Schedule object to serialize
	 * @return Serialized version of schedule object
	 */
	public String serialize(Schedule sched) {
		byte[] serSched = serializeScheduleToHardware(sched);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < serSched.length; i++)
			sb.append(serSched[i]).append(",");
		
		return sb.toString();
	}
	
	/**
	 * Produces a schedule object given a string serialization
	 * of a previous schedule object.
	 * 
	 * @param ser Serialized version of schedule object
	 * @return De-serialized schedule object
	 */
	public Schedule deSerialize(String ser) {
		Schedule sched = new Schedule();
		try {
			String[] entries = ser.split(",");
			for(int i = 0; i < entries.length; i++) {
				int day = i / 24;
				int hour = i % 24;
				byte hourSched = Byte.parseByte(entries[i]);
				for(int segment = 0; segment < 6; segment++)
					sched.schedule[day][6 * hour + segment] =
						((1 << segment) & hourSched) > 0;
			}
		}
		catch(Throwable e) {}
		return sched;
	}
}