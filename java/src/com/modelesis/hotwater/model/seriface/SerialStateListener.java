/**
 * 
 */
package com.modelesis.hotwater.model.seriface;

/**
 * Defines a listener so that
 * other objects can register
 * to receive serial communications
 * events.
 * 
 * @see SerialInterface
 * @author ramon
 *
 */
public interface SerialStateListener {

	/**
	 * Called whenever the serial state
	 * changes.
	 * 
	 * @param state Updated serial state
	 * @param info Additional information
	 */
	public void updateSerialState(SerialState state, String info);
}
