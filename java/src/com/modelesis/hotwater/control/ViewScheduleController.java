/**
 * 
 */
package com.modelesis.hotwater.control;

import com.modelesis.hotwater.model.ScheduleManager;

/**
 * The ViewScheduleController class
 * realizes the View Schedule use case.
 * 
 * @author ramon
 *
 */
public class ViewScheduleController extends UseCaseController {
	
	/**
	 * Creates a new instance of the controller.
	 * 
	 * @param mgr Associated schedule manager
	 */
	public ViewScheduleController(ScheduleManager mgr) {
		super(mgr);
	}
	
	/**
	 * Determines whether heater is meant to be on
	 * during the specified segment.
	 * 
	 * @param day Week day
	 * @param segment Segment
	 * @return Whether heater will run at specified time
	 */
	public boolean isHeaterOn(int day, int segment) {
		return scheduleManager.get(day, segment);
	}
	
	/**
	 * Retrieves the last selected day.
	 * 
	 * @return index of last individual column the user clicked over
	 */
	public int getSelectedDay() {
		return selectedDay;
	}
}