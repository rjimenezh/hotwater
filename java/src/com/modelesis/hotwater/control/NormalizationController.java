/**
 * 
 */
package com.modelesis.hotwater.control;

import com.modelesis.hotwater.model.ScheduleManager;

/**
 * The NormalizationController class 
 * realizes the normalization use cases
 * (Normalize Work Days, Normalize Weekends, Normalize Week)
 * 
 * @author ramon
 *
 */
public class NormalizationController extends UseCaseController {

	/**
	 * Creates a new instance of the controller.
	 * 
	 * @param mgr Associated schedule manager
	 */
	public NormalizationController(ScheduleManager mgr) {
		super(mgr);
	}
	
	public boolean canNormalize() {
		return (selectedDay > 0);
	}
	
	public void normalizeWorkDays() {
		scheduleManager.normalizeWeekDays(selectedDay);
	}
	
	public void normalizeWeekends() {
		scheduleManager.normalizeWeekends(selectedDay);
	}
	
	public void normalizeWeek() {
		scheduleManager.normalizeWeek(selectedDay);
	}
}