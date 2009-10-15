/**
 * 
 */
package com.modelesis.hotwater.model.seriface;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * Implements the interface
 * with PC serial ports used
 * to transfer data to the
 * HotWater device. 
 * 
 * @author ramon
 *
 */
public class SerialInterface {
	
	/** Milliseconds to wait for on port open. */
	private static final int OPEN_WAIT_MS = 100;
	
	/** Baud rate to talk to device. */
	private static final int BAUD_RATE = 57600;
	
	/** Probe data sent to serial ports. */
	private static final byte[] HOTWATER_PROBE = {(byte)'H', (byte)'W', (byte)'P'};
	
	/** Read wait timeout in milliseconds. */
	private static final int READ_WAIT = 2500;
	
	/** String expected from attached HotWater device when probing. */
	private static final byte[] HOTWATER_FOUND = {
		(byte)'H', (byte)'W', (byte)'P', (byte)1};
	
	/** Event listener. */
	protected SerialStateListener listener;
	
	/**
	 * Associates the specified listener
	 * to the serial interface implementation.
	 * 
	 * @param listener Listener to bind
	 */
	public void setListener(SerialStateListener listener) {
		this.listener = listener;
	}
	
	/**
	 * Probes all serial ports of the PC,
	 * looking for an attached HotWater device.
	 * 
	 * @return COM port identifier where a
	 * 	HotWater device was detected, or
	 * 	<tt>null</tt> if no such device was detected.
	 */
	@SuppressWarnings("unchecked")
	public CommPortIdentifier probeForHotWater() {
		notifyState(SerialState.LISTING_PORTS, null);
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier hotWaterPort = null;
		while(ports.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier)ports.nextElement();
			if(portId.isCurrentlyOwned())
				continue;
			hotWaterPort = probePort(portId);
			if(hotWaterPort != null)
				break;
		}
		
		return hotWaterPort;
	}
	
	/**
	 * Transfers the specified data to
	 * the HotWater device on the specified port.
	 * 
	 * @param port Port where the HotWater device listens
	 * @param data Data to send to the device
	 */
	public void transferData(CommPortIdentifier port, byte[] data) {
		
	}
	
	/**
	 * Probes the specified port to
	 * determine if a HotWater device
	 * is attached to it.
	 * 
	 * @param portId Port ID to probe
	 * @return portId if a HotWater was detected at it, <tt>null</tt> otherwise.
	 */
	private CommPortIdentifier probePort(CommPortIdentifier portId) {
		CommPortIdentifier hotWaterPort = null;
		CommPort port = null;
		
		try {
			notifyState(SerialState.OPENING_PORT, portId.getName());
			port = portId.open("HotWaterPCSoft", OPEN_WAIT_MS);
			if(port instanceof SerialPort) {
				notifyState(SerialState.PROBING_PORT, portId.getName());
				SerialPort serialPort = (SerialPort)port;
				serialPort.setSerialPortParams(BAUD_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
				OutputStream os = port.getOutputStream();
				// Wait - Arduino is resetting...
				try {
					Thread.sleep(READ_WAIT);
				} catch(InterruptedException ignored) {}
				os.write(HOTWATER_PROBE, 0, 3);
				os.flush();
				// Give Arduino time to loop and process
				try {
					Thread.sleep(READ_WAIT);
				} catch(InterruptedException ignored) {}

				InputStream is = port.getInputStream();
				if(is.available() >= HOTWATER_FOUND.length) {
					boolean found = true;
					for(int i = 0; i < HOTWATER_FOUND.length; i++) {
						int read = is.read();
						if(read != HOTWATER_FOUND[i]) {
							found = false;
							break;
						}
					}
					
					if(found) {
						hotWaterPort = portId;
						notifyState(SerialState.HOTWATER_FOUND, portId.getName());
					}
				}
			}
		}
		catch (PortInUseException ignored) { ignored.printStackTrace(); }
		catch (UnsupportedCommOperationException ignored) { ignored.printStackTrace(); }
		catch (IOException ignored) { ignored.printStackTrace(); }
		finally {
			if(port != null)
				port.close();
		}
		
		return hotWaterPort;
	}
	
	/**
	 * Notifies the associated listener,
	 * if any, of changes in the serial
	 * interface state.
	 * 
	 * @param state State update to notify
	 * @param info Additional state information
	 */
	private void notifyState(SerialState state, String info) {
		if(listener != null)
			listener.updateSerialState(state, info);
	}
}