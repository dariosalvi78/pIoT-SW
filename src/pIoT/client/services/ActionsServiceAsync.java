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

import com.google.gwt.user.client.rpc.AsyncCallback;

import pIoT.shared.messages.ActionMessage;
import pIoT.shared.notifications.Notification;

/**
 * Async interface for {@link ActionsService}
 * @author Dario Salvi
 *
 */
public interface ActionsServiceAsync {

	public void sendNotification(Notification n, AsyncCallback<Void> callback);
	
	public void sendMessage(ActionMessage mess, AsyncCallback<Void> callback);

	public void fixNotification(Notification n, AsyncCallback<Void> callback);

}
