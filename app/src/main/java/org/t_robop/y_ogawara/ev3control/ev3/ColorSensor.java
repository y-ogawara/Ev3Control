package org.t_robop.y_ogawara.ev3control.ev3;


/**
 * Color sensor (can apply to both of nxt and org.t_robop.y_ogawara.ev3remoteapp.ev3)
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 17-Aug-2013
 */
public class ColorSensor {

	SensorPort sensor;
	int type = EV3Protocol.COLOR;
	int mode = EV3Protocol.COL_REFLECT;

	/**
	 * @param sensor
	 *            e.g. SensorPort.S1
	 */
	public ColorSensor(SensorPort sensor) {
		this.sensor = sensor;
		// Check the sensor is nxt's or org.t_robop.y_ogawara.ev3remoteapp.ev3's
		if (sensor.getModeName(0).equals("NXT-COL-REF")) {
			type = EV3Protocol.NXT_COLOR;
		}
		sensor.setTypeAndMode(type, mode);	// See device definition
	}

	/**
	 * Returns light reading as a percentage.
	 * 
	 * @return 0 to 100 (0 = dark, 100 = bright)
	 */
	public int getLightPercent() {
		if (mode != EV3Protocol.COL_REFLECT) {
			mode = EV3Protocol.COL_REFLECT;
			sensor.setTypeAndMode(type, mode);
		}
		return sensor.readPercentValue();
	}
	
	/**
	 * Returns ambient light reading as a percentage.
	 * 
	 * @return 0 to 100 (0 = dark, 100 = bright)
	 */
	public int getAmbientLightPercent() {
		if (mode != EV3Protocol.COL_AMBIENT) {
			mode = EV3Protocol.COL_AMBIENT;
			sensor.setTypeAndMode(type, mode);
		}
		return sensor.readPercentValue();
	}
	
	/**
	 * Returns color as a percentage.
	 * 
	 * @return 0 to 100 
	 */
	public int getColorPercent() {
		if (mode != EV3Protocol.COL_COLOR) {
			mode = EV3Protocol.COL_COLOR;
			sensor.setTypeAndMode(type, mode);
		}
		return sensor.readPercentValue();
	}
	
	/**
	 * Returns color.
	 * 
	 * @return 0 to 7
	 * 0 - colorless, 1 - black, 2 - blue, 3 - green,
	 * 4 - yellow, 5 - red, 6 - white, 7 - brown.
	 * NXT's sensor cannot detect 0, 7.\
	 */
	public int getColor() {
		if (mode != EV3Protocol.COL_COLOR) {
			mode = EV3Protocol.COL_COLOR;
			sensor.setTypeAndMode(type, mode);
		}
		return (int)sensor.readSiValue();
	}
}
