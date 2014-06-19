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
import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.notifications.Notification;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Dario Salvi
 *
 */
@RemoteServiceRelativePath("DB")
public interface DBService extends RemoteService {
	
	/**
	 * Gets the class names stored as {@link DataMessage}.
	 * @return a list of full qualified class names
	 */
	public ArrayList<String> getDataMessageClassNames();
	
	/**
	 * Gets the number of instances of a given the classname.
	 * @param className a full qualified class name
	 * @return number of instances
	 */
	public int getClassStoredCount(String className);
	
	/**
	 * Retrieves all the data sent by a pIoT device.
	 * @param className the name of the class that implements the message
	 * @param deviceName the name of the pIoT device,
	 * if null, data from all devices will be retrieved
	 * @param limitstart an index that limits the results from start,
	 * the minimum value is 0, if -1 it is ignored
	 * @param limitend an index that limits the results to this index,
	 * if -1 it is ignored
	 * @return a list, ordered by time in descending way
	 */
	ArrayList<DataMessage> getDataMessages(String className, String deviceName, int limitstart, int limitend) throws DataBaseException, IllegalArgumentException;
	
	/**
	 * Retrieves all the devices in the DB.
	 * @return a list of devices instances
	 */
	ArrayList<Node> getDevices() throws DataBaseException;
	
	/**
	 * Stores or updates a device
	 * @param dev the device to be updated
	 */
	public void updateDevice(Node dev) throws DataBaseException;
	
	/**
	 * Gets the notifications that have not been fixed yet.
	 */
	ArrayList<Notification> getUnfixedNotifications() throws DataBaseException;
	
	/**
	 * Marks a notification as fixed
	 */
	void fixNotification(Notification n) throws DataBaseException;
	
	
}
