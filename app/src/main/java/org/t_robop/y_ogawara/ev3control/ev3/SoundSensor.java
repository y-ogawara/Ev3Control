package org.t_robop.y_ogawara.ev3control.ev3;


/**
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 17-Aug-2013
 */
public class SoundSensor {

	SensorPort sensor;
	int type = EV3Protocol.NXT_SOUND;
	int mode = EV3Protocol.SOUND_DB;
	
	/**
	 * Used to detect the loudness of sounds in the environment.
	 * NXT sound sensor.
	 * @param sensor
	 *            e.g. SensorPort.S1
	 */
	public SoundSensor(SensorPort sensor) {
		this.sensor = sensor;
		sensor.setTypeAndMode(type, mode);
	}

	/**
	 * Returns the decibels measured by the sound sensor.
	 * 
	 * @return dB - decibels
	 */
	public float getdB() {
		if (mode != EV3Protocol.SOUND_DB) {
			mode = EV3Protocol.SOUND_DB;
			sensor.setTypeAndMode(type, mode);
		}
		return sensor.readSiValue();
	}

	/**
	 * Returns sound within the human hearing frequency range, normalized by
	 * A-weighting. Extremely high frequency or low frequency sounds are not
	 * detected with this filtering (regardless of loudness).
	 * 
	 * @return dB(A) - decibels with A-weighting
	 */
	public float getdBA() {
		if (type != EV3Protocol.SOUND_DBA) {
			type = EV3Protocol.SOUND_DBA;
			sensor.setTypeAndMode(type, mode);
		}
		return sensor.readSiValue();
	}
}
