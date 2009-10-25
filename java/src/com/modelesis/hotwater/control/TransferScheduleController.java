/**
 * 
 */
package com.modelesis.hotwater.control;

import java.util.prefs.Preferences;

import gnu.io.CommPortIdentifier;

import com.modelesis.hotwater.model.ScheduleManager;
import com.modelesis.hotwater.model.seriface.SerialInterface;
import com.modelesis.hotwater.model.seriface.SerialState;
import com.modelesis.hotwater.model.seriface.SerialStateListener;

/**
 * Realizes the Transfer Schedule use case.
 * 
 * @author ramon
 *
 */
public class TransferScheduleController extends UseCaseController
implements SerialStateListener {
	
	/** Preferences key for device auto-detection. */
	private static final String AUTO_DETECT = "AUTO_DETECT_HOTWATER";
	
	/** Preferences key for last valid port. */
	private static final String LAST_VALID_PORT = "HW_LAST_VALID_PORT";
	
	/** Serial interface. */
	protected SerialInterface serialInterface;
	
	/** Event listener. */
	protected TransferScheduleListener listener;
	
	/** Port where HotWater has been detected. */
	protected CommPortIdentifier detectedPort;
	
	/** Worker thread for asynchronous operations. */
	protected Thread worker;
	
	/**
	 * Creates a new instance of the controller.
	 * 
	 * @param schedMgr Associated schedule manager
	 * @param serialMgr Associated serial interface manager
	 */
	public TransferScheduleController(ScheduleManager schedMgr,
	SerialInterface serialIface) {
		super(schedMgr);
		serialInterface = serialIface;
		serialInterface.setListener(this);
	}
	
	/**
	 * Allows the specified listener to receive
	 * schedule transfer events.
	 * 
	 * @param listener Listener to notify of transfer events
	 */
	public void setListener(TransferScheduleListener listener) {
		this.listener = listener;
	}
	
	/**
	 * Display the transfer dialog.
	 */
	public void showTransferDialog() {
		Preferences prefs =
			Preferences.userNodeForPackage(TransferScheduleController.class);
		boolean autoDetect = prefs.getBoolean(AUTO_DETECT, true);
		String lastValidPort = prefs.get(LAST_VALID_PORT, "");
		listener.updateSerialPortList(serialInterface.listSerialPorts());
		listener.updateSettingsAndDisplay(autoDetect, lastValidPort);
	}
	
	/**
	 * Detects a HotWater device among the computer's
	 * serial ports, returning the port name.
	 * 
	 * @return Port where a HotWater was found, or
	 * 	<tt>null</tt> if no HotWater was found attached
	 */
	public void detectDevice() {
		worker = new Thread() {
			public void run() {
				detectedPort = serialInterface.probeForHotWater();
			}
		};
		worker.start();
	}
	
	/**
	 * Initiates the schedule transfer process.
	 * 
	 * @param port Port to transfer schedule over
	 */
	public void transferData(final String port) {
		worker = new Thread() {
			public void run() {
				scheduleManager.transferData(serialInterface, port);
			}
		};
		worker.start();		
	}
	
	/**
	 * Cancels the current operation.
	 */
	public void cancel() {
		if(worker != null)
			worker.interrupt();
	}
	
	/**
	 * Persists the specified transfer settings.
	 * Invoked upon successful transfer completion.
	 * 
	 * @param autoDetect Whether auto-detect should be the default option
	 * @param lastValidPort Last port where a HotWater device was
	 * 	successfully programmed
	 */
	public void persistSettings(boolean autoDetect, String lastValidPort) {
		Preferences prefs =
			Preferences.userNodeForPackage(TransferScheduleController.class);
		prefs.putBoolean(AUTO_DETECT, autoDetect);
		if(lastValidPort != null)
			prefs.put(LAST_VALID_PORT, lastValidPort);
	}
	
	/**
	 * @see SerialStateListener#updateSerialState(SerialState, String).
	 */
	@Override
	public void updateSerialState(SerialState state, String info) {
		listener.transferEvent(state, info);
	}	
}