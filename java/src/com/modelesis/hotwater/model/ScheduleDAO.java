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
	
	public byte[] serializeSchedule(Schedule sched) {
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
}