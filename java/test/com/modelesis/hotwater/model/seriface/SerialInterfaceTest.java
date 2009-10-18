/**
 * 
 */
package com.modelesis.hotwater.model.seriface;

import junit.framework.TestCase;

import com.modelesis.hotwater.model.Schedule;
import com.modelesis.hotwater.model.ScheduleDAO;

/**
 * @author ramon
 *
 */
public class SerialInterfaceTest extends TestCase {

	/**
	 * Test method for {@link com.modelesis.hotwater.model.seriface.SerialInterface#probeForHotWater()}.
	 */
	public void testProbeForHotWater() {
		SerialInterface serialIface = new SerialInterface();
		MockSerialStateListener listener = new MockSerialStateListener();
		serialIface.setListener(listener);
		//System.err.println(serialIface.probeForHotWater());
	}

	/**
	 * Test method for {@link com.modelesis.hotwater.model.seriface.SerialInterface#transferData(gnu.io.CommPortIdentifier, byte[])}.
	 */
	public void testTransferData() {
		Schedule sched = new Schedule();
		sched.toggle(0, 42);
		sched.toggle(0, 43);
		sched.toggle(0, 44);
		sched.toggle(0, 45);
		sched.toggle(0, 46);
		sched.toggle(0, 47);
		SerialInterface serialIface = new SerialInterface();
		MockSerialStateListener listener = new MockSerialStateListener();
		serialIface.setListener(listener);
		ScheduleDAO dao = new ScheduleDAO();
		byte[] data = dao.serializeForTransfer(sched);
		serialIface.transferData("COM7", data);
	}
}
