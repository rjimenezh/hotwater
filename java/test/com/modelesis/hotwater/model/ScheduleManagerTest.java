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

}
