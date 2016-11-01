package org.t_robop.y_ogawara.ev3control.ev3;

import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * EV3Command contains easily accessible commands for the Lego EV3.
 * 
 * @author <a href="mailto:bbagnall@mts.net">Brian Bagnall</a>
 * @version 0.3 23-August-2006
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 17-Aug-2013
 */
public class EV3Command implements EV3Protocol {
	
	private static EV3Command singleton = new EV3Command();
	
	private EV3Comm ev3Comm;
	
	// Ensure no one tries to instantiate this.
	private EV3Command() {
	}
	
	public boolean setOutputState(byte[] request) {
		// 3 bytes for headers
		ByteArrayBuffer buffer = new ByteArrayBuffer(request.length + 3);
		
		buffer.append(DIRECT_COMMAND_NOREPLY); // Command Types
		
		// Reply size should be 0.
		byte[] replySize = {
			0x00, 0x00
		};
		buffer.append(replySize, 0, replySize.length);
		
		// Append the request
		buffer.append(request, 0, request.length);
		
		return sendRequest(buffer.toByteArray());
	}
	
	private String getString(byte[] request) {
		String result = null;
		
		ev3Comm.sendData(request);
		byte[] reply = ev3Comm.readData();
		
		// 0x04 is false, 0x02 is true.
		if (reply[2] == DIRECT_COMMAND_SUCCESS) {
			// Read the value and convert to String
			byte[] data = Arrays.copyOfRange(reply, 3, reply.length);
			result = new String(data);
		}
		else {
			return null;
		}
		
		Log.d("EV3Command", result);
		
		return result.trim();
	}
	
	public String getInputName(int port) {
		ByteArrayBuffer buffer = new ByteArrayBuffer(10);
		
		buffer.append(DIRECT_COMMAND_REPLY); // Command Types
		
		// Set maximum reply size 16 bytes.
		byte maxLength = 0x10;
		byte[] replySize = {
			(byte) (maxLength - 1), 0x00
		};
		buffer.append(replySize, 0, replySize.length);
		
		// Append the request
		byte[] request = {
			INPUT_DEVICE, GET_NAME, LAYER_MASTER, (byte) port, maxLength,
			UNKNOWN
		};
		buffer.append(request, 0, request.length);
		
		return getString(buffer.toByteArray());
	}
	
	/**
	 * @param port
	 * @return
	 * TODO implements
	 */
//	public int[] getTypeAndMode(int port) {
//		ByteArrayBuffer buffer = new ByteArrayBuffer(10);
//		
//		buffer.append(DIRECT_COMMAND_REPLY); // Command Types
//		
//		// Reply size is 4 bytes.
//		byte[] replySize = {
//			0x0A, 0x00
//		};
//		buffer.append(replySize, 0, replySize.length);
//		
//		// Append the request
//		byte[] request = {
//			INPUT_DEVICE, GET_TYPEMODE, LAYER_MASTER, (byte) port,
//		};
//		buffer.append(request, 0, request.length);
//		
//		ev3Comm.sendData(buffer.toByteArray());
//		
//		byte[] reply = ev3Comm.readData();
//		
//		int[] tam = new int[2];
//		
//		// 0x04 is false, 0x02 is true.
//		if (reply[2] == DIRECT_COMMAND_SUCCESS) {
//			tam[0] = reply[3];
//			tam[1] = reply[4];
//		}
//		
//		Log.d("EV3Command", "reply[2]: " + reply[2] + ", reply[3]: " + reply[3] + ", reply[4]: " + reply[4]);
//		return tam;
//	}
	
	public String getSymbol(int port) {
		ByteArrayBuffer buffer = new ByteArrayBuffer(10);
		
		buffer.append(DIRECT_COMMAND_REPLY); // Command Types
		
		// Set maximum reply size 16 bytes.
		byte maxLength = 0x10;
		byte[] replySize = {
			(byte) (maxLength - 1), 0x00
		};
		buffer.append(replySize, 0, replySize.length);
		
		// Append the request
		byte[] request = {
			INPUT_DEVICE, GET_SYMBOL, LAYER_MASTER, (byte) port, maxLength,
			UNKNOWN
		};
		buffer.append(request, 0, request.length);
		
		return getString(buffer.toByteArray());
	}
	
	public String getModeName(int port, int mode) {
		ByteArrayBuffer buffer = new ByteArrayBuffer(10);
		
		buffer.append(DIRECT_COMMAND_REPLY); // Command Types
		
		// Set maximum reply size 16 bytes.
		byte maxLength = 0x10;
		byte[] replySize = {
			(byte) (maxLength - 1), 0x00
		};
		buffer.append(replySize, 0, replySize.length);
		
		// Append the request
		byte[] request = {
			INPUT_DEVICE, GET_MODENAME, LAYER_MASTER, (byte) port, (byte) mode,
			maxLength, UNKNOWN
		};
		buffer.append(request, 0, request.length);
		
		return getString(buffer.toByteArray());
	}
	
	public InputValues getInputSiValues(int port, int type, int mode) {
		ByteArrayBuffer buffer = new ByteArrayBuffer(10);
		
		buffer.append(DIRECT_COMMAND_REPLY); // Command Types
		
		// Reply size is 4 bytes.
		byte[] replySize = {
			0x04, 0x00
		};
		buffer.append(replySize, 0, replySize.length);
		
		// Append the request
		byte[] request = {
			INPUT_READSI, LAYER_MASTER, (byte) port, (byte) type, (byte) mode,
			UNKNOWN
		};
		buffer.append(request, 0, request.length);
		
		ev3Comm.sendData(buffer.toByteArray());
		
		byte[] reply = ev3Comm.readData();
		
		InputValues inputValues = new InputValues();
		// 0x04 is false, 0x02 is true.
		inputValues.valid = (reply[2] == DIRECT_COMMAND_SUCCESS);
		
		// Read the SI unit value in float type
		byte[] data = Arrays.copyOfRange(reply, 3, reply.length);
		inputValues.siUnitValue = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		return inputValues;
	}
	
	public InputValues getInputPercentValues(int port, int type, int mode) {
		ByteArrayBuffer buffer = new ByteArrayBuffer(10);
		
		buffer.append(DIRECT_COMMAND_REPLY); // Command Types
		
		// Reply size is 1 byte.
		byte[] replySize = {
			0x01, 0x00
		};
		buffer.append(replySize, 0, replySize.length);
		
		// Append the request
		byte[] request = {
			INPUT_READ, LAYER_MASTER, (byte) port, (byte) type, (byte) mode,
			UNKNOWN
		};
		buffer.append(request, 0, request.length);
		
		ev3Comm.sendData(buffer.toByteArray());
		
		byte[] reply = ev3Comm.readData();
		
		InputValues inputValues = new InputValues();
		// 0x04 is false, 0x02 is true.
		inputValues.valid = (reply[2] == DIRECT_COMMAND_SUCCESS);
		
		// Read the percent value in float type
		inputValues.percentValue = (short) reply[3];
		return inputValues;
	}
	
	/**
	 * Small helper method to send request to EV3 and return verification
	 * result.
	 * 
	 * @param request
	 * @return
	 */
	private boolean sendRequest(byte[] request) throws RuntimeException {
		boolean verify = true; // default of 0 means success
		try {
			ev3Comm.sendData(request);
		}
		catch (RuntimeException e) {
			verify = false;
		}
		
		return verify;
	}
	
	public static void open() throws Exception {
		singleton.ev3Comm = AndroidComm.getInstance();
		try {
			singleton.ev3Comm.open();
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	public static void close() {
		// Set all motors to be stopped.
		byte[] request = {
			OUTPUT_STOP, LAYER_MASTER, ALL_MOTORS, BRAKE
		};
		singleton.setOutputState(request);
		// Close the connection.
		singleton.ev3Comm.close();
	}
	
	public static EV3Command getSingleton() {
		return singleton;
	}
	
}
