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

import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.notifications.Notification;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of {@link DBService}
 * @author Dario Salvi
 *
 */
public interface DBServiceAsync {
	
	void getDataMessageClassNames(AsyncCallback<ArrayList<String>> callback);
	
	void getClassStoredCount(String classname, AsyncCallback<Integer> callback);

	void getDataMessages(String className, String deviceName, int limitstart, int limitend, AsyncCallback<ArrayList<DataMessage>> callback);
	
	void getDevices(AsyncCallback<ArrayList<Node>> callback);
	
	void updateDevice(Node dev, AsyncCallback<Void> callback);
	
	void getUnfixedNotifications(AsyncCallback<ArrayList<Notification>> callback);
	
	void fixNotification(Notification n, AsyncCallback<Void> callback);
}
