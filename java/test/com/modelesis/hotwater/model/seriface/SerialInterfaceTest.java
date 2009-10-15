/**
 * 
 */
package com.modelesis.hotwater.model.seriface;

import com.modelesis.hotwater.model.seriface.SerialInterface;

import junit.framework.TestCase;

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
		System.err.println(serialIface.probeForHotWater());
	}

	/**
	 * Test method for {@link com.modelesis.hotwater.model.seriface.SerialInterface#transferData(gnu.io.CommPortIdentifier, byte[])}.
	 */
	public void testTransferData() {
	}
}
