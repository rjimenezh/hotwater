/**
 * 
 */
package com.modelesis.hotwater.model;

import junit.framework.TestCase;

/**
 * @author ramon
 *
 */
public class ScheduleTest extends TestCase {

	/**
	 * Test method for {@link com.modelesis.hotwater.model.Schedule#toggle(int, int)}.
	 */
	public void testToggle() {
		Schedule sched = new Schedule();
		sched.toggle(0, 0);
		assertTrue("toggleStatus is broken (1)", sched.get(0, 0));
		sched.toggle(0, 0);
		assertFalse("toggleStatus is broken (2)", sched.get(0, 0));
		sched.toggle(0, 143);
		assertTrue("toggleStatus is broken (3)", sched.get(0, 143));
		sched.toggle(6, 143);
		assertTrue("toggleStatus is broken (3)", sched.get(6, 143));
	}

	/**
	 * Test method for {@link com.modelesis.hotwater.model.Schedule#get(int, int)}.
	 */
	public void testGet() {
	}

	/**
	 * Test method for {@link com.modelesis.hotwater.model.Schedule#clear()}.
	 */
	public void testClear() {
		Schedule sched = new Schedule();
		sched.toggle(0, 0);
		sched.toggle(0, 143);
		sched.toggle(6, 143);
		sched.toggle(3, 81);
		sched.clear();
		assertFalse("clearAll is broken (1)", sched.get(0, 0));
		assertFalse("clearAll is broken (2)", sched.get(0, 143));
		assertFalse("clearAll is broken (3)", sched.get(6, 143));
		assertFalse("clearAll is broken (4)", sched.get(3, 81));
		assertFalse("clearAll is broken (5)", sched.get(0, 1));
		assertFalse("clearAll is broken (6)", sched.get(1, 0));
	}

	/**
	 * Test method for {@link com.modelesis.hotwater.model.Schedule#normalizeWeekDays(int)}.
	 */
	public void testNormalizeWeekDays() {
		Schedule sched = new Schedule();
		sched.toggle(1, 42); // Mondays 7:00 AM...
		sched.toggle(1, 43);
		sched.toggle(1, 44); // ... to 7:30 AM
		sched.normalizeWeekDays(1);
		assertTrue("normalizeWeekDays is broken (1)", sched.get(2, 42));
		assertTrue("normalizeWeekDays is broken (2)", sched.get(3, 43));
		assertTrue("normalizeWeekDays is broken (3)", sched.get(4, 43));
		assertTrue("normalizeWeekDays is broken (4)", sched.get(5, 44));
		assertFalse("normalizeWeekDays is broken (5)", sched.get(6, 42));
		assertFalse("normalizeWeekDays is broken (6)", sched.get(0, 44));
	}

	/**
	 * Test method for {@link com.modelesis.hotwater.model.Schedule#normalizeWeekends(int)}.
	 */
	public void testNormalizeWeekends() {
		Schedule sched = new Schedule();
		sched.toggle(0, 57); // Sundays 9:30 AM...
		sched.toggle(0, 58); // ... to 9:40 AM
		sched.normalizeWeekends(0);
		assertTrue("normalizeWeekends is broken (1)", sched.get(6, 57));
		assertTrue("normalizeWeekends is broken (2)", sched.get(6, 58));
		assertFalse("normalizeWeekends is broken (3)", sched.get(1, 1));
		//
		sched.clear();
		sched.toggle(6, 57); // Saturdays 9:30 AM...
		sched.toggle(6, 58); // ... to 9:40 AM
		sched.normalizeWeekends(6);
		assertTrue("normalizeWeekends is broken (4)", sched.get(0, 57));
		assertTrue("normalizeWeekends is broken (5)", sched.get(0, 58));
		assertFalse("normalizeWeekends is broken (6)", sched.get(1, 1));
	}

	/**
	 * Test method for {@link com.modelesis.hotwater.model.Schedule#normalizeWeek(int)}.
	 */
	public void testNormalizeWeek() {
		Schedule sched = new Schedule();
		sched.toggle(1, 42); // Mondays 7:00 AM...
		sched.toggle(1, 43);
		sched.toggle(1, 44); // ... to 7:30 0AM
		sched.normalizeWeek(1);
		assertTrue("normalizeWeek is broken (1)", sched.get(0, 42));
		assertTrue("normalizeWeek is broken (2)", sched.get(2, 43));
		assertTrue("normalizeWeek is broken (3)", sched.get(3, 44));
		assertTrue("normalizeWeek is broken (4)", sched.get(4, 44));
		assertTrue("normalizeWeek is broken (5)", sched.get(5, 43));
		assertTrue("normalizeWeek is broken (6)", sched.get(6, 42));
		assertTrue("normalizeWeek is broken (7)", sched.get(1, 42));
		assertFalse("normalizeWeek is broken (8)", sched.get(0, 100));
		assertFalse("normalizeWeek is broken (9)", sched.get(2, 25));
		assertFalse("normalizeWeek is broken (10)", sched.get(5, 13));
		assertFalse("normalizeWeek is broken (11)", sched.get(6, 96));
		
	}
	
	/**
	 * Test method for {@link com.modelesis.hotwater.model.Schedule#getMemento()}.
	 */
	public void testGetMemento() {
		Schedule origSched = new Schedule();
		Schedule clonedSched = new Schedule();
		origSched.toggle(1, 42);
		ScheduleMemento memento = origSched.getMemento();
		clonedSched.restoreStatus(memento);
		assertTrue("Memento is broken (1)", clonedSched.get(1, 42));
		assertFalse("Memento is broken (2)", clonedSched.get(0, 15));
	}
	
	/**
	 * Test method for {@link com.modelesis.hotwater.model.Schedule#restoreStatus(ScheduleMemento)}.
	 */
	public void testRestoreMemento() {
	}
	
	/**
	 * Test method for {@link com.modelesis.hotwater.model.Schedule#copyInto(Schedule)}.
	 */
	public void testCopyInto() {
		Schedule origSched = new Schedule();
		origSched.toggle(1, 42);
		origSched.toggle(1, 43);
		Schedule clonedSched = new Schedule();
		origSched.copyInto(clonedSched);
		assertEquals("copyInto/equals are broken (1)", origSched, clonedSched);
		clonedSched.toggle(1, 43);
		assertFalse("equals is broken (1)", origSched.equals(clonedSched));
		assertFalse("equals is broken (1)", clonedSched.equals(origSched));
	}
}