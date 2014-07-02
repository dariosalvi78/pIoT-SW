/**
 * 
 */
package pIoT.shared.messages;

import java.util.Date;

/**
 * @author Dario
 *
 */
public class HelloMessage extends DataMessage {

	private float Vcc;
	private float temperature;
	
	public HelloMessage() {
	}
	
	
	public HelloMessage(Date receivedTimestamp, String sourceMessage,
			int sourceAddress, float vcc, float temperature) {
		super(receivedTimestamp, sourceMessage, sourceAddress);
		Vcc = vcc;
		this.temperature = temperature;
	}


	public float getVcc() {
		return Vcc;
	}

	public void setVcc(float vcc) {
		Vcc = vcc;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

}
