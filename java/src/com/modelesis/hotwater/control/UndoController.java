/**
 * 
 */
package com.modelesis.hotwater.control;

import com.modelesis.hotwater.model.ScheduleManager;

/**
 * The UndoController realizes
 * the Undo use case.
 * 
 * @author ramon
 */
public class UndoController extends UseCaseController {

	/**
	 * Creates a new instance of the controller.
	 * 
	 * @param mgr Associated schedule manager
	 */
	public UndoController(ScheduleManager mgr) {
		super(mgr);
	}
	
	/**
	 * Determines whether the schedule has undoable
	 * changes, in order to update the UI accordingly.
	 * 
	 * @return Whether undoable changes have been made
	 */
	public boolean canUndo() {
		return scheduleManager.canUndo();
	}
	
	/**
	 * Undoes the last change to the schedule.
	 */
	public void undo() {
		scheduleManager.undo();
	}
}