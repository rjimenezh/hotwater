/**
 * 
 */
package com.modelesis.hotwater.control;

import com.modelesis.hotwater.model.ScheduleChangeListener;
import com.modelesis.hotwater.model.ScheduleManager;

/**
 * The UseCaseController class defines the
 * base class of use case controllers.
 * 
 * @author ramon
 *
 */
public abstract class UseCaseController {
	
	/** Associated schedule manager object. */
	protected ScheduleManager scheduleManager;
	
	/** Selected day - the day of the week the user last clicked over. */
	protected static int selectedDay;
	
	/**
	 * Constructs a new schedule controller object.
	 * 
	 * @param mgr Schedule manager to associate controller to.
	 */
	public UseCaseController(ScheduleManager mgr) {
		scheduleManager = mgr;
	}

	/**
	 * Registers the specified listener
	 * as a schedule change listener.
	 * 
	 * @param lst Listener to bind to schedule
	 */
	public void addScheduleChangeListener(ScheduleChangeListener lst) {
		scheduleManager.addScheduleChangeListener(lst);
	}
}