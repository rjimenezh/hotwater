/**
 * 
 */
package com.modelesis.hotwater.model;

import junit.framework.TestCase;

/**
 * @author ramon
 *
 */
public class ScheduleManagerTest extends TestCase {

	/**
	 * Test method for {@link com.modelesis.hotwater.model.ScheduleManager#undo()}.
	 */
	public void testUndo() {
		ScheduleManager mgr = new ScheduleManager();
		mgr.toggle(1, 43);
		mgr.toggle(1, 43);
		mgr.undo();
		assertTrue("Undo is broken (1)", mgr.get(1, 43));
		mgr.undo();
		assertFalse("Undo is broken (2)", mgr.get(1, 43));
	}
	
	/**
	 * Test method for {@link com.modelesis.hotwater.model.ScheduleManager#canUndo()}.
	 */
	public void testCanUndo() {
		ScheduleManager mgr = new ScheduleManager();
		assertFalse("canUndo() is broken (1)", mgr.canUndo());
		mgr.toggle(1, 43);
		assertTrue("canUndo() is broken (2)", mgr.canUndo());
		mgr.undo();
		assertFalse("canUndo() is broken (3)", mgr.canUndo());
	}
	
	/**
	 * Test method for {@link com.modelesis.hotwater.model.ScheduleManager#saveSchedule()}.
	 */
	public void testLoadAndSave() {
		ScheduleManager mgr = new ScheduleManager();
		mgr.toggle(1, 43);
		mgr.saveSchedule();
		mgr = new ScheduleManager();
		mgr.loadSchedule();
		assertTrue("Load/save are broken (1)", mgr.get(1, 43));
		assertFalse("Load/save are broken (2)", mgr.get(5, 109));
	}

}
