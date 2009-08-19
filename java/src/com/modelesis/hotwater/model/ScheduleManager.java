/**
 * 
 */
package com.modelesis.hotwater.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * The ScheduleManager class defines
 * the external (component) interface
 * of the scheduling mechanism.
 * 
 * @author ramon
 *
 */
public class ScheduleManager {
	
	/** Underlying schedule. */
	protected Schedule schedule;
	
	/** Mementos used for undo and redo. */
	protected List<ScheduleMemento> mementos;
	
	/**
	 * Creates a new instance of ScheduleManager.
	 */
	public ScheduleManager() {
		schedule = new Schedule();
		mementos = new ArrayList<ScheduleMemento>();
	}

	/**
	 * Binds the specified schedule change listener
	 * with the managed schedule.
	 * 
	 * @param lst Schedule change listener to bind to schedule
	 */
	public void setScheduleChangeListener(ScheduleChangeListener lst) {
		schedule.setScheduleChangeListener(lst);
	}
	
	/**
	 * Delegate method for {@link Schedule#get(int, int)}
	 * 
	 * @param weekDay Weekday: 0 - Sunday, 6 - Saturday
	 * @param segment Segment: 0 - 00:00/00:09; 143 - 23:50/23:59
	 * @return Whether heater power is on at the specified slot
	 */
	public boolean get(int weekDay, int segment) {
		return schedule.get(weekDay, segment);
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
	 * Delegate method for {@link Schedule#normalizeWeek(int)}
	 * 
	 * @param weekDay Day of week whose programming will be replicated
	 */
	public void normalizeWeek(int weekDay) {
		ScheduleMemento memento = schedule.getMemento();
		schedule.normalizeWeek(weekDay);
		mementos.add(memento);
	}

	/**
	 * Delegate method for {@link Schedule#normalizeWeekDays(int)}
	 * 
	 * @param weekDay Day of week whose programming will be replicated
	 */
	public void normalizeWeekDays(int weekDay) {
		ScheduleMemento memento = schedule.getMemento();
		schedule.normalizeWeekDays(weekDay);
		mementos.add(memento);
	}

	/**
	 * Delegate method for {@link Schedule#normalizeWeekends(int)}
	 * 
	 * @param weekDay Day of week whose programming will be replicated
	 */
	public void normalizeWeekends(int weekDay) {
		ScheduleMemento memento = schedule.getMemento();
		schedule.normalizeWeekends(weekDay);
		mementos.add(memento);
	}

	/**
	 * Delegate method for {@link Schedule#toggle(int, int)}
	 * 
	 * @param weekDay Weekday: 0 - Sunday, 6 - Saturday 
	 * @param segment Segment: 0 - 00:00/00:09; 143 - 23:50/23:59
	 */
	public void toggle(int weekDay, int segment) {
		ScheduleMemento memento = schedule.getMemento();
		schedule.toggle(weekDay, segment);
		mementos.add(memento);
	}
	
	/**
	 * Undoes latest change to the schedule.
	 */
	public void undo() {
		int lastMemento = mementos.size();
		if(lastMemento > 0) {
			lastMemento--;
			schedule.restoreStatus(mementos.get(lastMemento));
			mementos.remove(lastMemento);
		}
	}
}