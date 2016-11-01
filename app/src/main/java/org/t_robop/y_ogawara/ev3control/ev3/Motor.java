package org.t_robop.y_ogawara.ev3control.ev3;

/**
 * Motor class. Contains three instances of Motor. Usage: Motor.A.forward(5000);
 * 
 * @author <a href="mailto:bbagnall@mts.net">Brian Bagnall</a>
 * @version 0.2 4-September-2006
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 17-Aug-2013
 */
public class Motor implements EV3Protocol {
	
	private static final EV3Command ev3Command = EV3Command.getSingleton();
	
	private byte id;
	private byte power;
	private byte mode;
	
	/**
	 * Motor A.
	 */
	public static final Motor A = new Motor((byte) 0x01);
	/**
	 * Motor B.
	 */
	public static final Motor B = new Motor((byte) 0x02);
	/**
	 * Motor C.
	 */
	public static final Motor C = new Motor((byte) 0x04);
	/**
	 * Motor D.
	 */
	public static final Motor D = new Motor((byte) 0x08);
	
	private Motor(byte id) {
		this.id = id;
		this.power = 20; // 20 power by default
		this.mode = BRAKE; // Brake mode
	}
	
	/**
	 * Get the ID of the motor. One of 'A', 'B', 'C' or 'D'.
	 */
	public final char getId() {
		char port = 'A';
		switch (id) {
			case 0x01:
				port = 'A';
				break;
			case 0x02:
				port = 'B';
				break;
			case 0x04:
				port = 'C';
				break;
			case 0x08:
				port = 'D';
				break;
		}
		return port;
	}
	
	/**
	 * Causes motor to rotate forward.
	 * 
	 * @return Error value. true means success. false means fail.
	 */
	public boolean forward() {
		byte[] request = {
			OUTPUT_POWER, LAYER_MASTER, id, power,
			OUTPUT_START, LAYER_MASTER, id
		};
		return ev3Command.setOutputState(request);
	}
	
	/**
	 * Causes motor to rotate backward.
	 * 
	 * @return Error value. true means success. false means fail.
	 */
	public boolean backward() {
		byte[] request = {
			OUTPUT_POWER, LAYER_MASTER, id, negative(power),
			OUTPUT_START, LAYER_MASTER, id
		};
		return ev3Command.setOutputState(request);
	}
	
	private byte negative(byte power) {
		return (byte) (-power & 0x3f); // 0x3f (= 00111111) is the mask
	}
	
	/**
	 * Sets motor speed.
	 * NOTE: this method doesn't send commands to EV3. To update the speed,
	 * use forward or backward method following this.
	 * 
	 * @param speed
	 *            (0 ~ 100 %)
	 */
	public void setSpeed(int speed) {
		if (speed > 100 | speed < 0) return;
		speed = (int) (speed / 100.0 * 31); // 0 ~ 31
		this.power = (byte) speed;
	}
	
	/**
	 * Gets the current motor speed.
	 * 
	 * @return speed (0 ~ 100 %)
	 */
	public int getSpeed() {
		return (this.power / 31) * 100;
	}
	
	/**
	 * Stops the motor using brakes.
	 * 
	 * @return Error value. true means success. false means fail.
	 */
	public boolean stop() {
		byte[] request = {
			OUTPUT_STOP, LAYER_MASTER, id, mode
		};
		return ev3Command.setOutputState(request);
	}
}
