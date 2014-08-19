/**
 * pIoT Server.
 * A server for:
 * <ul>
 * <li> storing data from pIoT nodes
 * <li> sending commands to pIoT nodes
 * <li> viewing and interpreting data
 * <li> setting up rules for reacting to events
 * </ul>
 * License: GNU GENERAL PUBLIC LICENSE Version 3
 * http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Package containing messages used in the example sketches of pIoT-FW.
 */
package pIoT.shared.messages.examples;

import java.util.Date;

import pIoT.shared.messages.DataMessage;

/**
 * Hello Message used in the example sketches of pIoT
 * @author Dario Salvi
 *
 */
public class Hello extends DataMessage {

	private float Vcc;
	private float temperature;
	private long operationTime;
	private int unsentMessages;
	
	public Hello() {
	}
	
	
	public Hello(Date receivedTimestamp, String sourceMessage,
			int sourceAddress, float vcc, float temperature, long operationTime, int unsentMessages) {
		super(receivedTimestamp, sourceMessage, sourceAddress);
		this.Vcc = vcc;
		this.temperature = temperature;
		this.operationTime = operationTime;
		this.unsentMessages = unsentMessages;
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

	public long getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(long operationTime) {
		this.operationTime = operationTime;
	}
	
	public int getUnsentMessages(){
		return this.unsentMessages;
	}
	
	public void setUnsentMessages(int ums){
		this.unsentMessages = ums;
	}
}
