package org.t_robop.y_ogawara.ev3control.ev3;

/**
 * Port class. Contains 4 Port instances.<br>
 * Usage: Port.S4.readSiValue();
 * 
 * @author <a href="mailto:bbagnall@mts.net">Brian Bagnall</a>
 * @version 0.3 29-October-2006
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 17-Aug-2013
 */
public class SensorPort implements EV3Protocol {
	
	private static final EV3Command ev3Command = EV3Command.getSingleton();
	
	private int id;
	private int type;
	private int mode;
	
	public static SensorPort S1 = new SensorPort(0);
	public static SensorPort S2 = new SensorPort(1);
	public static SensorPort S3 = new SensorPort(2);
	public static SensorPort S4 = new SensorPort(3);
	
	private SensorPort(int port) {
		id = port;
		type = TYPE_DEFAULT;
		mode = MODE_DEFAULT;
	}
	
	public void setTypeAndMode(int type, int mode) {
		this.type = type;
		this.mode = mode;
		// Skip the first value
		ev3Command.getInputPercentValues(id, type, mode); 
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the name of the sensor.
	 * 
	 * @return Name String
	 */
	public String getName() {
		return ev3Command.getInputName(id);
	}
	
//	/**
//	 * Gets the type and the mode of the sensor.
//	 * 
//	 * @return type and mode
//	 */
//	public int[] getTypeAndMode() {
//		return ev3Command.getTypeAndMode(id);
//	}
	
	/**
	 * Gets the mode name of the sensor.
	 * 
	 * @return Name String
	 */
	public String getModeName(int mode) {
		return ev3Command.getModeName(id, mode);
	}
	
	
	/**
	 * Gets the symbol of the sensor. (eg. cm, deg, etc.)
	 * 
	 * @return Symbol String
	 */
	public String getSymbol() {
		return ev3Command.getSymbol(id);
	}
	
	/**
	 * Reads the SI unit value of the sensor.
	 * 
	 * @return SI unit sensor value.
	 */
	public float readSiValue() {
		InputValues vals = ev3Command.getInputSiValues(id, type, mode);
		return vals.siUnitValue;
	}
	
	/**
	 * Reads the percent value of the sensor.
	 * 
	 * @return Percent sensor value.
	 */
	public int readPercentValue() {
		InputValues vals = ev3Command.getInputPercentValues(id, type, mode);
		return vals.percentValue;
	}
}
