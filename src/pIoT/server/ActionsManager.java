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
 * Package containing server side functionalities.
 */
package pIoT.server;

import java.util.Date;

import pIoT.shared.notifications.Notification;

/**
 * Manages different types of actions.
 * @author Dario Salvi
 *
 */
public class ActionsManager {

	/**
	 * A notification is an action that delivers the notification to the user
	 * on the Home page
	 */
	public static void manage(Notification not){
		
		//Construct an example by removing the timestamp
		Date originaldate = not.getCreated();
		not.setCreated(null);
		if(DB.getDB().queryByExample(not).size() ==0){
			//No similar notification, then set the timestamp back and save
			not.setCreated(originaldate);
			DB.getDB().store(not);
		}
		
	}
}
