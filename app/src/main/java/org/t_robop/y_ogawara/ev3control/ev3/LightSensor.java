package org.t_robop.y_ogawara.ev3control.ev3;


/**
 * NXT lights sensor.
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 17-Aug-2013
 */
public class LightSensor {

	SensorPort sensor;
	int type = EV3Protocol.NXT_LIGHT;
	int mode = EV3Protocol.COL_REFLECT;

	/**
	 * @param sensor
	 *            e.g. SensorPort.S1
	 */
	public LightSensor(SensorPort sensor) {
		this.sensor = sensor;
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
}
