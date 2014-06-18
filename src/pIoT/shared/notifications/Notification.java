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

import java.io.Serializable;
import java.util.Date;

/**
 * A generic notification.
 * @author Dario Salvi
 *
 */
public class Notification implements Serializable {

	/**
	 * The message of the notification
	 */
	private String message;
	
	/**
	 * The time the notification was created
	 */
	private Date created;
	
	/**
	 * If true, the notification has been fixed
	 */
	private boolean fixed;
	
	public Notification() {
	}

	public Notification(String message, Date created, boolean fixed) {
		super();
		this.message = message;
		this.created = created;
		this.fixed = fixed;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

}
