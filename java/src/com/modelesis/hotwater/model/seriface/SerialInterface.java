/**
 * 
 */
package com.modelesis.hotwater.model.seriface;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
	
	/** Arduino bootloader timeout in milliseconds. */
	private static final int RESET_WAIT = 2500;
	
	/** HotWater firmware loop wait in milliseconds. */
	private static final int LOOP_WAIT = 1000;
	
	/** String expected from attached HotWater device when probing. */
	private static final byte[] HOTWATER_FOUND = {
		(byte)'H', (byte)'W', (byte)'P', (byte)1};
	
	/** Transfer handshake sent to HotWater device. */
	private static final byte[] TRANSFER_HANDSHAKE = {(byte)'H', (byte)'W', (byte)'T'};
	
	/** String expected from attached HotWater device when transferring. */
	private static final byte[] TRANSFER_ACK = {
		(byte)'H', (byte)'W', (byte)'T', (byte)1};
	
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
	 * Lists the names of serial ports
	 * installed in this computer.
	 * 
	 * @return List of available serial port names.
	 */
	@SuppressWarnings("unchecked")
	public String[] listSerialPorts() {
		List<String> lstSerialPorts =
			new ArrayList<String>();
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		while(ports.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier)ports.nextElement();
			if(portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
				lstSerialPorts.add(portId.getName());
		}
		String[] serialPorts = new String[lstSerialPorts.size()];
		lstSerialPorts.toArray(serialPorts);
		return serialPorts;
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
			if(hotWaterPort != null) {
				notifyState(SerialState.HOTWATER_FOUND, hotWaterPort.getName());
			}
				break;
		}

		return hotWaterPort;
	}
	
	/**
	 * Transfers the specified data to
	 * the HotWater device on the specified port.
	 * 
	 * @param portName Port where the HotWater device listens
	 * @param data Data to send to the device
	 */
	public void transferData(String portName, byte[] data) {
		CommPort port = null;
		
		try {
			CommPortIdentifier portId =
				CommPortIdentifier.getPortIdentifier(portName);
			notifyState(SerialState.OPENING_PORT, portId.getName());
			port = portId.open("HotWaterPCSoft", OPEN_WAIT_MS);
			if(port instanceof SerialPort) {
				notifyState(SerialState.CONNECTING, portId.getName());
				SerialPort serialPort = (SerialPort)port;
				serialPort.setSerialPortParams(BAUD_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
				OutputStream os = port.getOutputStream();
				InputStream is = port.getInputStream();
				writeHandshake(os, TRANSFER_HANDSHAKE);
				if(readAcknowledge(is, TRANSFER_ACK)) {
					notifyState(SerialState.SENDING_DATA, portId.getName());
					os.write(data, 0, data.length);
					os.flush();
					// Wait for ack...
					try {
						Thread.sleep(LOOP_WAIT);
					} catch(InterruptedException ignored) {}
					if(readAcknowledge(is, TRANSFER_ACK)) {
						int bytes = is.read();
						if(bytes == data.length)
							notifyState(SerialState.DATA_SENT, null);
						else
							notifyState(SerialState.TRANSFER_ERROR, bytes + "/" + data.length);
					}
					else
						notifyState(SerialState.TRANSFER_ERROR, "Final ack missing");
				}
				else
					notifyState(SerialState.TRANSFER_ERROR, "Initial ack missing");
			}
		}
		catch (NoSuchPortException ignored) { ignored.printStackTrace(); }
		catch (PortInUseException ignored) { ignored.printStackTrace(); }		
		catch (UnsupportedCommOperationException ignored) { ignored.printStackTrace(); }
		catch (IOException ignored) { ignored.printStackTrace(); }
		finally {
			if(port != null)
				port.close();
		}
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
				InputStream is = port.getInputStream();
				writeHandshake(os, HOTWATER_PROBE);
				if(readAcknowledge(is, HOTWATER_FOUND)) {
					hotWaterPort = portId;
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
	 * Writes a handshake sequence to the specified
	 * output stream, considering Arduino bootloader
	 * reset and loop times.
	 * 
	 * @param os Output stream to write handshake to
	 * @param handShake Handshake data to send
	 */
	private void writeHandshake(OutputStream os, byte[] handShake) {
		// Wait - Arduino is resetting...
		try {
			Thread.sleep(RESET_WAIT);
		} catch(InterruptedException ignored) {}
		try {
			os.write(handShake, 0, 3);
			os.flush();
		} catch(IOException ignored) {}
		// Now wait some more...
		try {
			Thread.sleep(LOOP_WAIT);
		} catch(InterruptedException ignored) {}
	}
	
	/**
	 * Reads data from the specified input string
	 * and determines if it matches the specified
	 * acknowledge data.
	 * 
	 * @param is Input stream to read acknowledge from
	 * @param ackData Data to assert acknowledge against
	 * @return Whether an acknowledgment has been received
	 */
	private boolean readAcknowledge(InputStream is, byte[] ackData) {
		boolean found = true;
		
		try {
			if(is.available() < ackData.length)
				return false;
			for(int i = 0; i < ackData.length; i++) {
				int read = is.read();
				if(read != ackData[i]) {
					found = false;
					break;
				}
			}
		}
		catch(IOException ignored) {}
		
		return found;
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