/**
 * 
 */
package com.modelesis.hotwater.model;

/**
 * Defines a listener that gets
 * notified when a particular slot
 * in the schedule gets changed.
 * 
 * @author ramon
 */
public interface ScheduleChangeListener {
	
	/**
	 * Called whenever a particular slot
	 * in the schedule is toggled.
	 * 
	 * @param day  Weekday (0 = Sun, 6 = Sat)
	 * @param segment 10-minute segment within day
	 */
	public void scheduleChanged(int day, int segment);

}
