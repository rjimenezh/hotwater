/**
 * 
 */
package com.modelesis.hotwater.control;

import com.modelesis.hotwater.model.ScheduleManager;

/**
 * The SchedulePersistenceController class
 * realizes the schedule persistence use cases
 * (Load Schedule, Save Schedule).
 * 
 * @author ramon
 */
public class SchedulePersistenceController extends UseCaseController {

	/**
	 * Creates a new instance of the controller.
	 * 
	 * @param mgr Associated schedule manager
	 */
	public SchedulePersistenceController(ScheduleManager mgr) {
		super(mgr);
		// Load Schedule is implemented thus:
		scheduleManager.loadSchedule();
	}
}