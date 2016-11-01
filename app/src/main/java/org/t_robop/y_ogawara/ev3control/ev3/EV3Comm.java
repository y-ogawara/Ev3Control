package org.t_robop.y_ogawara.ev3control.ev3;

public interface EV3Comm {

	void open() throws Exception;

	public void sendData(byte[] request);

	public byte[] readData();

	public void close();

}