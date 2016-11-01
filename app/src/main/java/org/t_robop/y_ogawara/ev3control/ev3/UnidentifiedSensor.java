package org.t_robop.y_ogawara.ev3control.ev3;


/**
 * An unidentified sensor.
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 17-Aug-2013
 */
public class UnidentifiedSensor {
	
	SensorPort sensor;
	
	/**
	 * An unidentified sensor.
	 * 
	 * @param sensor
	 *            e.g. SensorPort.S1
	 */
	public UnidentifiedSensor(SensorPort sensor) {
		this.sensor = sensor;
	}
	
	/**
	 * Sets the type and mode of the sensor.
	 * 
	 * @param type
	 * @param mode
	 */
	public void setTypeAndMode(int type, int mode) {
		sensor.setTypeAndMode(type, mode);
	}
	
	/**
	 * Gets the name of the sensor.
	 * 
	 * @return
	 */
	public String getName() {
		return sensor.getName();
	}
	
	/**
	 * Gets the type and the mode of the sensor.
	 * @return 
	 */
//	public int[] getTypeAndMode() {
//		return sensor.getTypeAndMode();
//	}
	
	/**
	 * Gets the symbol of the sensor.
	 * 
	 * @return
	 */
	public String getSymbol() {
		return sensor.getSymbol();
	}
	
	/**
	 * Gets the SI unit value of the sensor.
	 * 
	 * @return
	 */
	public float getSiValue() {
		return sensor.readSiValue();
	}
	
	/**
	 * Gets the percent value of the sensor.
	 * 
	 * @return
	 */
	public int getPercentValue() {
		return sensor.readPercentValue();
	}
}
