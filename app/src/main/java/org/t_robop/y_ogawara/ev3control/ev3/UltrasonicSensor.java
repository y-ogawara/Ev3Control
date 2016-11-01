package org.t_robop.y_ogawara.ev3control.ev3;


/**
 * Ultrasonic sensor (can apply to both of nxt and org.t_robop.y_ogawara.ev3remoteapp.ev3)
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 1-Oct-2013
 */
public class UltrasonicSensor {

	SensorPort sensor;
	int type = EV3Protocol.ULTRASONIC;
	int mode = EV3Protocol.US_CM;

	/**
	 * @param sensor
	 *            e.g. SensorPort.S1
	 */
	public UltrasonicSensor(SensorPort sensor) {
		this.sensor = sensor;
		// Check the sensor is nxt's or org.t_robop.y_ogawara.ev3remoteapp.ev3's
		if (sensor.getModeName(0).equals("NXT-US-CM")) {
			type = EV3Protocol.NXT_ULTRASONIC;
		}
		sensor.setTypeAndMode(type, mode);	// See device definition
	}

	/**
	 * Returns the length between an obstacle ahead and org.t_robop.y_ogawara.ev3remoteapp.ev3 as a centimeter.
	 * 
	 * @return 0 to 255 cm.
	 */
	public int getCm() {
		if (mode != EV3Protocol.US_CM) {
			mode = EV3Protocol.US_CM;
			sensor.setTypeAndMode(type, mode);
		}
		return (int) sensor.readSiValue();
	}
	
	/**
	 * Returns the length between an obstacle ahead and org.t_robop.y_ogawara.ev3remoteapp.ev3 as a inch.
	 * 
	 * @return 0 to 100 inch.
	 */
	public int getInch() {
		if (mode != EV3Protocol.US_INCH) {
			mode = EV3Protocol.US_INCH;
			sensor.setTypeAndMode(type, mode);
		}
		return (int) sensor.readSiValue();
	}
}
