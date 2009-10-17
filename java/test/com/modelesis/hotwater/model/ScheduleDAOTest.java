package com.modelesis.hotwater.model;

import junit.framework.TestCase;

public class ScheduleDAOTest extends TestCase {
	
	/**
	 * Test method for {@link com.modelesis.hotwater.model.ScheduleDAO#serializeToShortArray(Schedule)}.
	 */
	public void testSerializeScheduleToByteArray() {
		Schedule sched = new Schedule();
		sched.toggle(1, 45);  // Monday 7:30 AM
		sched.toggle(1, 46);  // Monday 7:40 AM
		ScheduleDAO dao = new ScheduleDAO();
		byte[] res = dao.serializeToByteArray(sched);
		byte mondaySevenAM = res[31];
		assertEquals("DAO is broken (1)", 24, mondaySevenAM);
	}
	
	public void testSerialize() {
		Schedule sched = new Schedule();
		sched.toggle(1, 45);  // Monday 7:30 AM
		sched.toggle(1, 46);  // Monday 7:40 AM
		ScheduleDAO dao = new ScheduleDAO();
		String ser = dao.serialize(sched);
		Schedule restoredSched = dao.deSerialize(ser);
		assertTrue("DAO is broken (2)", restoredSched.get(1, 45));
		assertTrue("DAO is broken (3)", restoredSched.get(1, 46));
		assertFalse("DAO is broken (4)", restoredSched.get(3, 43));
	}
}
