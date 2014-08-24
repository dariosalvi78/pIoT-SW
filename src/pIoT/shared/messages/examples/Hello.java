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

	private float vcc;
	private float temperature;
	private long operationTime;
	private long sentMessages;
	private long unsentMessages;
	private long receivedMessages;

	public Hello() {
	}


	public Hello(Date receivedTimestamp, String sourceMessage,
			int sourceAddress, float vcc, float temperature,
			long operationTime, long sentMsgs, long unsentMsgs, long receivedMsgs) {
		super(receivedTimestamp, sourceMessage, sourceAddress);
		this.vcc = vcc;
		this.temperature = temperature;
		this.operationTime = operationTime;
		this.unsentMessages = unsentMsgs;
		this.sentMessages = sentMsgs;
		this.receivedMessages = receivedMsgs;
	}


	public float getVcc() {
		return vcc;
	}

	public void setVcc(float vcc) {
		this.vcc = vcc;
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

	public long getSentMessages() {
		return sentMessages;
	}

	public void setSentMessages(long sentMessages) {
		this.sentMessages = sentMessages;
	}

	public long getUnsentMessages() {
		return unsentMessages;
	}

	public void setUnsentMessages(long unsentMessages) {
		this.unsentMessages = unsentMessages;
	}

	public long getReceivedMessages() {
		return receivedMessages;
	}

	public void setReceivedMessages(long receivedMessages) {
		this.receivedMessages = receivedMessages;
	}
}
