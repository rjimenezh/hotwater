/**
 * 
 */
package com.modelesis.hotwater.control;

import com.modelesis.hotwater.model.seriface.SerialState;

/**
 * Defines events that the Transfer Schedule
 * UI may be interested in.
 * 
 * @author ramon
 *
 */
public interface TransferScheduleListener {
	
	/**
	 * Update the transfer settings and display them
	 * for user confirmation/edition.
	 * 
	 * @param autoDetect Whether auto-detect should be the default option
	 * @param lastValidPort Last port where a HotWater device was
	 * 	successfully programmed
	 */
	public void updateSettingsAndDisplay(boolean autoDetect, String lastValidPort);

	/**
	 * Signals that a transfer event has occurred.
	 * 
	 * @param state Serial interface state
	 * @param info  Additional event information
	 */
	public void transferEvent(SerialState state, String info);
	
	
	/**
	 * Instructs the listener to update
	 * the serial port list.
	 * 
	 * @param portList Updated serial port list.
	 */
	public void updateSerialPortList(String[] portList);
}
