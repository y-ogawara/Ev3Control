package org.t_robop.y_ogawara.ev3control.ev3;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Android Communicator.
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 17-Aug-2013
 */
public class AndroidComm implements EV3Comm {
	
	private final static String TAG = "AndroidComm";
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private BluetoothDevice mDevice;
	private BluetoothSocket mSocket;
	public static OutputStream mOutputStream;
	private InputStream mInputStream;
	
	static class SingletonHolder {
		static AndroidComm mmInstance = new AndroidComm();
	}
	
	public static AndroidComm getInstance() {
		return SingletonHolder.mmInstance;
	}
	
	private AndroidComm() {
		// Singleton class.
	}
	
	public void setDevice(BluetoothDevice device) {
		close();
		mDevice = device;
	}
	
	@Override
	public void open() throws IOException {
		if (mDevice == null) throw new IOException();
		
		// Orthodox method
		mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
		
		try {
			mSocket.connect();
		}
		catch (IOException firstIOException) {
			Log.d(TAG, "Failed to connect by orthodox method");
			// Thanks to Micael Biermann
			try {
				Method method = mDevice.getClass().getMethod("createRfcommSocket", new Class[] {
						int.class
				});
				mSocket = (BluetoothSocket) method.invoke(mDevice, Integer.valueOf(1));
				mSocket.connect();
			}
			catch (IOException secondIOException) {
				// Unable to connect; close the socket and get out
				try {
					mSocket.close();
				}
				catch (IOException closeException) {
					closeException.printStackTrace();
					Log.d(TAG, "it seems unable to recover");
				}
				throw secondIOException;
			}
			catch (Exception exception) {
				exception.printStackTrace();
				Log.d(TAG, "this exception should not be occured in release version");
			}
		}
		
		mOutputStream = mSocket.getOutputStream();
		mInputStream = mSocket.getInputStream();
	}
	
	@Override
	public void close() {
		
		if (mSocket != null) {
			try {
				if (mOutputStream != null) {
					mOutputStream.close();
				}
				if (mInputStream != null) {
					mInputStream.close();
				}
				mSocket.close();
			}
			catch (IOException e) {
				Log.e(TAG, "Failed to close connection.", e);
			}
		}
		mSocket = null;
	}
	
	@Override
	public byte[] readData() {
		byte[] buffer = new byte[2];
		byte[] result;
		int numBytes;
		try {
			// Calculate the size of response by reading 2 bytes.
			mInputStream.read(buffer, 0, buffer.length);
			// Reply size
			numBytes = (int) buffer[0] + (buffer[1] << 8);
			
			// Read the body of response
			result = new byte[numBytes];
			mInputStream.read(result, 0, numBytes);
			
		}
		catch (IOException e) {
			Log.e(TAG, "Read failed.", e);
			throw new RuntimeException(e);
		}
		Log.v(TAG, "Read: " + result);
		return result;
	}
	
	@Override
	public void sendData(byte[] request) throws RuntimeException {
		// Calculate the size of request and set them in little-endian order.
		// Next 2 bytes are identification codes. We can use them to identify
		// the pair of request and response.
		// (In current implementation, default is 0x00, 0x00)
		int bodyLength = request.length + 2; // add 2 for identification codes.
		byte[] header = {
			(byte) (bodyLength & 0xff), (byte) ((bodyLength >>> 8) & 0xff),
			0x00, 0x00
		};
		try {
			mOutputStream.write(header);
			mOutputStream.write(request);
		}
		catch (IOException e) {
			Log.e(TAG, "Send failed.", e);
			throw new RuntimeException(e);
		}
		Log.v(TAG, "Sent: " + request);
	}
	
}