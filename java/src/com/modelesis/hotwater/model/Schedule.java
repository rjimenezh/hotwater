/**
 * 
 */
package com.modelesis.hotwater.model;

import java.util.Calendar;

/**
 * Defines an operational schedule
 * for HotWater.  A schedule divides
 * the week in 10-minute segments; for
 * each such segment, a status for the
 * heater may be asserted.
 * 
 * @author ramon
 *
 */
public class Schedule {
	
	protected static final int WEEK_DAYS = 7;
	
	protected static final int SEGMENTS_PER_DAY = 144;
	
	public static final int MIN_WEEKDAY = 0;
	
	public static final int MAX_WEEKDAY = 6;
	
	public static final int MIN_SEGMENT = 0;
	
	public static final int MAX_SEGMENT = 143;
	
	/** Actual schedule - One bolean per weekday/segment coordinate. */
	protected boolean[][] schedule;
	
	/**
	 * Creates a new Schedule object.
	 */
	public Schedule() {
		schedule = new boolean[WEEK_DAYS][];
		for(int i = 0; i < WEEK_DAYS; i++)
			schedule[i] = new boolean[SEGMENTS_PER_DAY];
	}
	
	/**
	 * Returns the time representation
	 * as an integer indicating the current
	 * 10-minute segment within the week,
	 * ranging from 0 (Sunday 0:00-0:09)
	 * to 1,008 (Saturday 23:50-23:59)
	 * 
	 * @return Current time
	 */
	public int getTime() {
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);		
		return 144 * (dayOfWeek - 1) + 6 * hourOfDay + minutes / 10;
	}
	
	/**
	 * Toggles the heater power status for
	 * the specified week day and segment. 
	 * 
	 * @param weekDay Day of Week: Sunday = 0, Monday = 1, etc.
	 * @param segment Segment: 0 = 0:00-0:09; 1 = 0:10-0:19, etc.
	 */
	public void toggle(int weekDay, int segment) {
		validateInput(weekDay, segment);
		schedule[weekDay][segment] = !schedule[weekDay][segment];
	}
	
	/**
	 * Retrieves the heater power status
	 * for the specified week day and segment.
	 * 
	 * @param weekDay Day of Week: Sunday = 0, Monday = 1, etc.
	 * @param segment Segment: 0 = 0:00-0:09; 1 = 0:10-0:19, etc.
	 */
	public boolean get(int weekDay, int segment) {
		validateInput(weekDay, segment);
		return schedule[weekDay][segment];
	}
	
	/**
	 * Clears the entire schedule, i.e.
	 * turns off the heater for the entire week.
	 */
	public void clear() {
		for(int i = MIN_WEEKDAY; i <= MAX_WEEKDAY; i++)
			for(int j = MIN_SEGMENT; j <= MAX_SEGMENT; j++)
				schedule[i][j] = false;
	}
	
	/**
	 * Assigns the settings of the selected week day
	 * to all week (working) days in the schedule.
	 * 
	 * @param weekDay Week day (Mon-Fri) to normalize week schedule to
	 */
	public void normalizeWeekDays(int weekDay) {
		if((weekDay < 1) || (weekDay > 5))
			throw new IllegalArgumentException();
		for(int i = 1; i <= 5; i++)
			if(i != weekDay) {
				for(int j = MIN_SEGMENT; j <= MAX_SEGMENT; j++)
					schedule[i][j] = schedule[weekDay][j];
			}
	}
	
	/**
	 * Assigns the settings of the the selected weekend day
	 * to all weekend days in the schedule.
	 * 
	 * @param weekDay Weekend day (Sat, Sun) to normalize weekend schedule to
	 */
	public void normalizeWeekends(int weekDay) {
		if((weekDay != 0) && (weekDay != 6))
			throw new IllegalArgumentException();
		int i = 6 - weekDay;
		for(int j = MIN_SEGMENT; j <= MAX_SEGMENT; j++)
			schedule[i][j] = schedule[weekDay][j];
	}
	
	/**
	 * Assigns the settings of the selected week day
	 * to the entire schedule (all days).
	 * 
	 * @param weekDay Week day to normalize entire schedule to
	 */
	public void normalizeWeek(int weekDay) {
		validateInput(weekDay, 0);
		for(int i = MIN_WEEKDAY; i <= MAX_WEEKDAY; i++)
			if(i != weekDay) {
				for(int j = MIN_SEGMENT; j <= MAX_SEGMENT; j++)
					schedule[i][j] = schedule[weekDay][j];
			}
	}
	
	/**
	 * Validates input parameter
	 * 
	 * @param weekDay Day of Week: Sunday = 0, Monday = 1, etc.
	 * @param segment Segment: 0 = 0:00-0:09; 1 = 0:10-0:19, etc. (up to 143)
	 */
	protected void validateInput(int weekDay, int segment) {
		if((weekDay < MIN_WEEKDAY) || (weekDay > MAX_WEEKDAY))
			throw new IllegalArgumentException();
		if((segment < MIN_SEGMENT) || (segment > MAX_SEGMENT))
			throw new IllegalArgumentException();
	}
	
	/**
	 * Retrieves a ScheduleMemento to allow undoing/redoing
	 * changes to the schedule.
	 * 
	 * @return Memento with schedule's current state
	 */
	protected ScheduleMemento getMemento() {
		ScheduleMemento memento = new ScheduleMemento(schedule);
		return memento;
	}
	
	/**
	 * Restores the schedule from a given memento.
	 * 
	 * @param memento Memento to restore the schedule's state from
	 */
	protected void restoreStatus(ScheduleMemento memento) {
		for(int i = MIN_WEEKDAY; i <= MAX_WEEKDAY; i++)
			for(int j = MIN_SEGMENT; j <= MAX_SEGMENT; j++)
				schedule[i][j] = memento.schedule[i][j];
	}
}