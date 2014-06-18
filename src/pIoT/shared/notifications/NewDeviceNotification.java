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
 * Package containing the notifications to the user.
 */
package pIoT.shared.notifications;

import java.util.Date;

import pIoT.shared.Node;

/**
 * A notification that a new device has been discovered.
 * @author Dario Salvi
 *
 */
public class NewDeviceNotification extends Notification {

	private Node device;
	
	/**
	 * 
	 */
	public NewDeviceNotification() {
	}

	public NewDeviceNotification(Date created, boolean fixed, Node device) {
		super("Found new device with address "+device.getAddress()+" please set name and location", created, fixed);
		this.device = device;
	}

	public Node getDevice() {
		return device;
	}

	public void setDevice(Node device) {
		this.device = device;
	}
	
	

}
