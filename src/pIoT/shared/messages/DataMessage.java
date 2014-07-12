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
 * Package containing the interpretation of the messages received by pIoT devices.
 * All these classes have to be Reflectable and Serializable.
 */
package pIoT.shared.messages;

import java.io.Serializable;
import java.util.Date;

import org.dt.reflector.client.Reflectable;


/**
 * Base class for representing messages received from pIoT device.
 * @author Dario Salvi
 *
 */
public class DataMessage implements Serializable, Reflectable {

	/**
	 * The time and date the message was received.
	 */
	private Date receivedTimestamp;
	
	/**
	 * The source JSON message.
	 */
	private String sourceMessage;
	
	/**
	 * If the message contained an address of a device, here's the address.
	 */
	private int sourceAddress;
	
	public DataMessage() {
	}


	public DataMessage(Date receivedTimestamp, String sourceMessage,
			int sourceAddress) {
		super();
		this.receivedTimestamp = receivedTimestamp;
		this.sourceMessage = sourceMessage;
		this.sourceAddress = sourceAddress;
	}


	public Date getReceivedTimestamp() {
		return receivedTimestamp;
	}


	public void setReceivedTimestamp(Date receivedTimestamp) {
		this.receivedTimestamp = receivedTimestamp;
	}


	public String getSourceMessage() {
		return sourceMessage;
	}


	public void setSourceMessage(String sourceMessage) {
		this.sourceMessage = sourceMessage;
	}


	public int getSourceAddress() {
		return sourceAddress;
	}


	public void setSourceAddress(int sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	
	
}
