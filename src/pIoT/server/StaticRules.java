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

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

import pIoT.server.SerialServiceImpl.DataHandler;
import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.notifications.NewDeviceNotification;

/**
 * Some static rules to manage incoming data.
 * @author Dario Salvi
 *
 */
public class StaticRules  {

	private static Logger logger = Logger.getLogger(StaticRules.class.getName());
	
	public StaticRules(){
		SerialServiceImpl.addDataHandler(new DataHandler() {
			
			@Override
			public void handle(DataMessage m) throws Exception {
				//update devices
				int devAddr = m.getSourceAddress();
				if(devAddr!= 0){
					manageDevice(devAddr);
				}
			}
		});
		logger.info("Static rules started");
	}
	
	private void manageDevice(int address){
		Query query = DBServiceImpl.getDB().query();
		query.constrain(Node.class);
		query.descend("address").constrain(address);
		ObjectSet<Node> obset = null;
		try{
			obset = query.execute();
		} catch(Exception ex) {
			//Nothing to do
			logger.log(Level.WARNING, "Error while trying to look for existing device with address "+address, ex);
		}

		if((obset == null) || (obset.size() == 0)) {
			//Store the new device
			Node newdevice =new Node();
			newdevice.setAddress(address);
			newdevice.setLastContact(Calendar.getInstance().getTime());
			DBServiceImpl.getDB().store(newdevice);

			//Generate notification
			NewDeviceNotification notif = new NewDeviceNotification(Calendar.getInstance().getTime(),
					false, newdevice);
			ActionsServiceImpl.storeNotification(notif);
		} else {
			//The device already exists, just update the lastcontact field
			Node dev = obset.get(0); //should be only one
			dev.setLastContact(Calendar.getInstance().getTime()); //update last contact
			DBServiceImpl.getDB().store(dev);
		}
	}

}
