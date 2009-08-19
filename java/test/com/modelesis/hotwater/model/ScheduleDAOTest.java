package com.modelesis.hotwater.model;

import junit.framework.TestCase;

public class ScheduleDAOTest extends TestCase {
	
	/**
	 * Test method for {@link com.modelesis.hotwater.model.ScheduleDAO#serializeScheduleToHardware(Schedule)}.
	 */
	public void testSerializeScheduleToHardware() {
		Schedule sched = new Schedule();
		sched.toggle(1, 45);  // Monday 7:30 AM
		sched.toggle(1, 46);  // Monday 7:40 AM
		ScheduleDAO dao = new ScheduleDAO();
		byte[] res = dao.serializeScheduleToHardware(sched);
		byte mondaySevenAM = res[31];
		assertEquals("DAO is broken (1)", 24, mondaySevenAM);
	}
}
