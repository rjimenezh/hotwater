/**
 * 
 */
package com.modelesis.hotwater.control;

import com.modelesis.hotwater.model.ScheduleManager;

/**
 * The ToggleScheduleController class
 * realizes the Toggle Schedule use case.
 * 
 * @author ramon
 */
public class ToggleScheduleController extends UseCaseController {
	
	/**
	 * Creates a new instance of the controller.
	 * 
	 * @param mgr Associated schedule manager
	 */
	public ToggleScheduleController(ScheduleManager mgr) {
		super(mgr);
	}
	
	/**
	 * Toggles the heater schedule at the specified time.
	 * 
	 * @param day Week day
	 * @param segment Segment
	 */
	public void toggleSchedule(int day, int segment) {
		scheduleManager.toggle(day, segment);
	}
	
	/**
	 *
	 * Updates the last selected day.
	 * 
	 * @param selectedDay Index of last individual column the user clicked over
	 */
	@SuppressWarnings("static-access")
	public void setSelectedDay(int selectedDay) {
		this.selectedDay = selectedDay;
	}
}