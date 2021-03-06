/**
 * 
 */
package com.modelesis.hotwater.model.seriface;

/**
 * Defines possible serial states
 * for {@link SerialInterface}.
 * 
 * @author ramon
 *
 */
public enum SerialState {
	LISTING_PORTS,
	OPENING_PORT,
	PROBING_PORT,
	HOTWATER_FOUND,
	DETECTION_FAILED,
	CONNECTING,
	SENDING_DATA,
	DATA_SENT,
	TRANSFER_ERROR
}
