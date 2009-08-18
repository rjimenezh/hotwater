/**
 * 
 */
package com.modelesis.hotwater.model;

/**
 * Defines a Memento for the Schedule class.
 * 
 * @author ramon
 */
public class ScheduleMemento {
	
	/** Back-up schedule data. */
	protected boolean[][] schedule;
	
	/**
	 * Creates a new ScheduleMemento object.
	 * 
	 * @param schedule Schedule to back up.
	 */
	public ScheduleMemento(boolean[][] _schedule) {
		schedule = new boolean[Schedule.WEEK_DAYS][];
		int i = 0;
		for(; i < Schedule.WEEK_DAYS; i++)
			schedule[i] = new boolean[Schedule.SEGMENTS_PER_DAY];
		for(i = 0; i < Schedule.WEEK_DAYS; i++)
			for(int j = 0; j < Schedule.SEGMENTS_PER_DAY; j++)
				schedule[i][j] = _schedule[i][j];
	}
}
