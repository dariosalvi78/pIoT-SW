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

import pIoT.shared.messages.ControlMessage;

/**
 * An Error indicates unwanted behaviours in the system.
 * @author Dario Salvi
 *
 */
public class Error extends ControlMessage {

	/**
	 * Indicates the degree of severity:
	 * 0: just debugging information
	 * 1: warning (an indication of a possible
	 * unwanted situation that does not jeopardise the system)
	 * 2: severe (a situation that jeopardises the system)
	 */
	private int severity;
	
	private String message;
	
	public Error() {
	}

	public Error(Date receivedTimestamp, String sourceMessage, int severity, String message) {
		super(receivedTimestamp, sourceMessage);
		this.severity = severity;
		this.message = message;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
