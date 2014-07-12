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

import pIoT.shared.messages.ActionMessage;
import pIoT.shared.messages.DataMessage;

/**
 * A switch message, as used in pIoT-FW examples
 * @author Dario Salvi
 *
 */
public class SwitchState extends DataMessage {

	boolean on;
	
	public SwitchState() {
	}
	
	public SwitchState(Date receivedTimestamp, String sourceMessage,
			int sourceAddress, boolean status) {
		super(receivedTimestamp, sourceMessage, sourceAddress);
		this.on = status;
	}



	public boolean isOn() {
		return on;
	}

	public void setOn(boolean status) {
		this.on = status;
	}

}
