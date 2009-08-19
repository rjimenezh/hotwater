/**
 * 
 */
package com.modelesis.hotwater.model;

import java.util.ArrayList;
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
	 * Delegate method for {@link Schedule#getTime()}
	 * 
	 * @return Time as segment of the week
	 */
	public int getTime() {
		return schedule.getTime();
	}

	/**
	 * Delegate method for {@link Schedule#normalizeWeek(int)}
	 * 
	 * @param weekDay Day of week whose programming will be replicated
	 */
	public void normalizeWeek(int weekDay) {
		mementos.add(schedule.getMemento());
		schedule.normalizeWeek(weekDay);
	}

	/**
	 * Delegate method for {@link Schedule#normalizeWeekDays(int)}
	 * 
	 * @param weekDay Day of week whose programming will be replicated
	 */
	public void normalizeWeekDays(int weekDay) {
		mementos.add(schedule.getMemento());
		schedule.normalizeWeekDays(weekDay);
	}

	/**
	 * Delegate method for {@link Schedule#normalizeWeekends(int)}
	 * 
	 * @param weekDay Day of week whose programming will be replicated
	 */
	public void normalizeWeekends(int weekDay) {
		mementos.add(schedule.getMemento());
		schedule.normalizeWeekends(weekDay);
	}

	/**
	 * Delegate method for {@link Schedule#toggle(int, int)}
	 * 
	 * @param weekDay Weekday: 0 - Sunday, 6 - Saturday 
	 * @param segment Segment: 0 - 00:00/00:09; 143 - 23:50/23:59
	 */
	public void toggle(int weekDay, int segment) {
		mementos.add(schedule.getMemento());
		schedule.toggle(weekDay, segment);
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