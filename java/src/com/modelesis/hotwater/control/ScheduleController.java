/**
 * 
 */
package com.modelesis.hotwater.control;

import com.modelesis.hotwater.model.ScheduleChangeListener;
import com.modelesis.hotwater.model.ScheduleManager;

/**
 * The ScheduleController class defines
 * the application's main controller.
 * 
 * @author ramon
 */
public class ScheduleController {
	
	/** Associated schedule manager object. */
	protected ScheduleManager scheduleManager;
	
	/**
	 * Constructs a new schedule controller object.
	 * 
	 * @param mgr Schedule manager to associate controller to.
	 */
	public ScheduleController(ScheduleManager mgr) {
		scheduleManager = mgr;
	}	
	
	/**
	 * Binds a schedule change listener to the underlying manager.
	 * 
	 * @param lst Listener to bind to manager.
	 */
	public void addScheduleChangeListener(ScheduleChangeListener lst) {
		scheduleManager.addScheduleChangeListener(lst);
	}

	/**
	 * Determines whether the schedule has operations that can be undone.
	 * 
	 * @return Whether undo is enabled.
	 */
	public boolean canUndo() {
		return scheduleManager.canUndo();
	}

	/**
	 * Normalizes the entire week programming
	 * to that of the specified weekday.
	 * 
	 * @param weekDay
	 */
	public void normalizeWeek(int weekDay) {
		scheduleManager.normalizeWeek(weekDay);
	}

	/**
	 * Normalizes week days programming
	 * to that of the specified weekday.
	 * 
	 * @param weekDay
	 */
	public void normalizeWeekDays(int weekDay) {
		scheduleManager.normalizeWeekDays(weekDay);
	}

	/**
	 * Normalizes weekend programming
	 * to that of the specified weekday.
	 * @param weekDay
	 */
	public void normalizeWeekends(int weekDay) {
		scheduleManager.normalizeWeekends(weekDay);
	}

	/**
	 * Undoes last change performed to schedule.
	 */
	public void undo() {
		scheduleManager.undo();
	}
}