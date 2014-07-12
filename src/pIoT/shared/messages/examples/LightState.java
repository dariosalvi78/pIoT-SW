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
 * Light message, used in the example sketches of the pIoT
 * @author Dario Salvi
 *
 */
public class LightState extends DataMessage {
	
	private int intensity;
	
	public LightState(){
		
	}
	
	public LightState(Date receivedTimestamp, String sourceMessage,
			int sourceAddress, int intensity) {
		super(receivedTimestamp, sourceMessage, sourceAddress);
		this.intensity = intensity;
	}

	public int getIntensity() {
		return intensity;
	}

	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}

}
