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
 * Package containing the definition of the client/server services.
 */
package pIoT.client.services;

import java.util.ArrayList;

import pIoT.shared.DataBaseException;
import pIoT.shared.SerialPortException;
import pIoT.shared.messages.ActionMessage;
import pIoT.shared.notifications.Notification;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * A service that manages actions including notifications, sending messages
 * to nodes, and others.
 * @author Dario Salvi
 *
 */
@RemoteServiceRelativePath("actions")
public interface ActionsService extends RemoteService {
	
	/**
	 * Retrieves a list of examples of action messages
	 * @return
	 */
	public ArrayList<ActionMessage> getActionMessageExamples();

	/**
	 * Generates a notification that will be shown on the web.
	 * @param n notification
	 */
	public void sendNotification(Notification n);
	
	/**
	 * Marks a notification as fixed
	 */
	public void fixNotification(Notification n) throws DataBaseException;

	/**
	 * Sends a message to a node
	 * @param mess the {@link ActionMessage} to be sent
	 * @throws SerialPortException 
	 */
	public void sendMessage(ActionMessage mess) throws SerialPortException;

}
