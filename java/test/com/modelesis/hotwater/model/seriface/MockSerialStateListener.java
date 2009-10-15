/**
 * 
 */
package com.modelesis.hotwater.model.seriface;

/**
 * Implements a serial state
 * listener for debugging purposes.
 * 
 * @author ramon
 *
 */
public class MockSerialStateListener implements SerialStateListener {

	/**
	 * @see com.modelesis.hotwater.model.seriface.SerialStateListener#updateSerialState(com.modelesis.hotwater.model.seriface.SerialState, java.lang.String)
	 */
	@Override
	public void updateSerialState(SerialState state, String info) {
		switch(state) {
		case LISTING_PORTS : System.err.println("Listing ports"); break;
		case OPENING_PORT  : System.err.println("Opening port " + info); break;
		case PROBING_PORT  : System.err.println("Probing port " + info); break;
		case HOTWATER_FOUND: System.err.println("HotWater found at " + info); break;
		}
	}
}
