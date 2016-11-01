package org.t_robop.y_ogawara.ev3control.ev3;

import android.util.Log;

/**
 * Touch sensor (can apply to both of nxt and org.t_robop.y_ogawara.ev3remoteapp.ev3)
 * 
 * @author BB
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 17-Aug-2013
 */
public class TouchSensor {

	SensorPort sensor;
	int type = EV3Protocol.TOUCH;
	int mode = EV3Protocol.TOUCH_TOUCH;

	/**
	 * @param sensor
	 *            e.g. SensorPort.S1
	 */
	public TouchSensor(SensorPort sensor) {
		this.sensor = sensor;
		// Check the sensor is nxt's or org.t_robop.y_ogawara.ev3remoteapp.ev3's
		if (sensor.getModeName(0).equals("NXT-TOUCH")) {
			type = EV3Protocol.NXT_TOUCH;
			Log.d("TouchSensor", "This is an nxt touch sensor.");
		}
		sensor.setTypeAndMode(type, mode);
	}

	/**
	 * 
	 * @return true if sensor is pressed.
	 */
	public boolean isPressed() {
		if (mode != EV3Protocol.TOUCH_TOUCH) {
			mode = EV3Protocol.TOUCH_TOUCH;
			sensor.setTypeAndMode(type, mode);
		}
		return (int)sensor.readSiValue() == 1;
	}
	
	/**
	 * 
	 * @return the number of bumped.
	 */
	public int getBumps() {
		if (mode != EV3Protocol.TOUCH_BUMPS) {
			mode = EV3Protocol.TOUCH_BUMPS;
			sensor.setTypeAndMode(type, mode);
		}
		return (int)sensor.readSiValue();
	}
}
