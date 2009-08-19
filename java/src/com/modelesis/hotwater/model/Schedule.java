/**
 * 
 */
package com.modelesis.hotwater.model;


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
	
	/** Number of days in a week. */
	protected static final int WEEK_DAYS = 7;
	
	/** Number of 10-minute segments in a day. */
	protected static final int SEGMENTS_PER_DAY = 144;
	
	/** Minimum weekday array index. */
	public static final int MIN_WEEKDAY = 0;
	
	/** Maximum weekday array index. */
	public static final int MAX_WEEKDAY = 6;
	
	/** Minimum segment array index. */
	public static final int MIN_SEGMENT = 0;
	
	/** Maximum segment array index. */
	public static final int MAX_SEGMENT = 143;
	
	/** Actual schedule - One bolean per weekday/segment coordinate. */
	protected boolean[][] schedule;
	
	/** Schedule change listener. */
	protected ScheduleChangeListener listener;
	
	/**
	 * Creates a new Schedule object.
	 */
	public Schedule() {
		schedule = new boolean[WEEK_DAYS][];
		for(int i = 0; i < WEEK_DAYS; i++)
			schedule[i] = new boolean[SEGMENTS_PER_DAY];
	}
	
	/**
	 * Associate a schedule change listener to this schedule.
	 * 
	 * @param lst Listener to bind to this schedule
	 */
	public void setScheduleChangeListener(ScheduleChangeListener lst) {
		listener = lst;
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
		notifyListener(weekDay, segment);
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
			for(int j = MIN_SEGMENT; j <= MAX_SEGMENT; j++) {
				schedule[i][j] = false;
				notifyListener(i, j);
			}
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
				for(int j = MIN_SEGMENT; j <= MAX_SEGMENT; j++) {
					schedule[i][j] = schedule[weekDay][j];
					notifyListener(i, j);
				}		
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
		for(int j = MIN_SEGMENT; j <= MAX_SEGMENT; j++) {
			schedule[i][j] = schedule[weekDay][j];
			notifyListener(i, j);
		}
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
				for(int j = MIN_SEGMENT; j <= MAX_SEGMENT; j++) {
					schedule[i][j] = schedule[weekDay][j];
					notifyListener(i, j);
				}
			}
	}
	
	/**
	 * Validates input parameters.
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
	
	protected void notifyListener(int weekDay, int segment) {
		if(listener != null)
			listener.scheduleChanged(weekDay, segment);
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