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
 * A Control Message is very similar to a {@link DataMessage}
 * but is used only for logging and debugging purposes.
 * @author Dario Salvi
 *
 */
public class ControlMessage implements Serializable, Reflectable {

	/**
	 * The time and date the message was received.
	 */
	private Date receivedTimestamp;
	
	/**
	 * The source JSON message.
	 */
	private String sourceMessage;
	
	
	public ControlMessage() {
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


	public ControlMessage(Date receivedTimestamp, String sourceMessage) {
		super();
		this.receivedTimestamp = receivedTimestamp;
		this.sourceMessage = sourceMessage;
	}

}
