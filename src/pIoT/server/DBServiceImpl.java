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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pIoT.client.services.DBService;
import pIoT.client.tests.ExtendedData;
import pIoT.client.tests.ExtendedDataMessage;
import pIoT.shared.DataBaseException;
import pIoT.shared.Node;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.notifications.Notification;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.StoredClass;
import com.db4o.query.Query;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import static pIoT.server.QueryUtils.limit;

public class DBServiceImpl extends RemoteServiceServlet implements DBService{

	private static ObjectContainer db;
	private static final String dbFileName = "DB";

	/**
	 * 
	 */
	public static synchronized ObjectContainer getDB() {
		if(db == null)
			db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), dbFileName);

		return db;
	}

	/**
	 * Deletes the DB entirely.
	 */
	public static void deleteDB() {
		db.close();
		new File(dbFileName).delete();
		db = null;
	}

	/**
	 * Stores and commits.
	 */
	public static void store(Object o){
		db.store(o);
		db.commit();
	}

	public DBServiceImpl() {
		super();

		///*
		Date now = Calendar.getInstance().getTime();
		Node dev1 = new Node(1, "dev1", "home");
		dev1.setLastContact(now);
		getDB().store(dev1);
		for(int i=0; i<10; i++){
			ExtendedData data = new ExtendedData();
			data.setABool(true);
			ArrayList<Integer> lst = new ArrayList<>();
			lst.add(i);
			lst.add(i+1);
			lst.add(i+2);
			data.setAList(lst);
			data.setAnArray(new int[]{1,2,3});
			ExtendedDataMessage edm = new ExtendedDataMessage(new Date(now.getTime() + i*100),
					"mess "+i, 1, "\nextended\n "+i, data);
			getDB().store(edm);
		}

		//*/
	}

	@Override
	public void destroy(){
		getDB().close();
		super.destroy();
	}

	public ArrayList<String> getDataMessageClassNames(){
		ArrayList<String> retval = new ArrayList<String>();
		StoredClass[] storedclasses = getDB().ext().storedClasses();
		for(StoredClass  stcl: storedclasses){
			String name = stcl.getName();
			if(isDataMessage(name))
				retval.add(name);
		}

		return retval;
	}

	public int getClassStoredCount(String className){
		StoredClass stc = getDB().ext().storedClass(className);
		if(stc == null)
			return 0;
		else return stc.instanceCount();
	}

	private boolean isDataMessage(String className){
		try {
			Class<?> clazz = Class.forName(className);
			return DataMessage.class.isAssignableFrom(clazz);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public ArrayList<DataMessage> getDataMessages(final String className, final String deviceName, final int limitstart, final int limitend)
			throws DataBaseException, IllegalArgumentException {
		Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("The class must be a DataMessage");
		}
		if(! DataMessage.class.isAssignableFrom(clazz))
			throw new IllegalArgumentException("The class must be a DataMessage");

		int devAddress = 0;
		if(deviceName != null){
			//Get the source address of the device
			Query query = getDB().query();
			query.constrain(Node.class);
			query.descend("name").constrain(deviceName);

			ObjectSet<Node> devicesset = query.execute();
			if(devicesset.isEmpty())
				throw new IllegalArgumentException("The name provided for the device "+deviceName+" does not exist");

			//Get the first element (only should exist)
			Node device = devicesset.next();
			devAddress = device.getAddress();
		}
		ArrayList<DataMessage> retval = new ArrayList<DataMessage>();

		//Query for the messages
		Query query= getDB().query();
		query.constrain(clazz);
		query.descend("receivedTimestamp").orderDescending();
		if(deviceName != null){
			query.descend("sourceAddress").constrain(devAddress);
		}

		ObjectSet<DataMessage> messagesset = query.execute();
		for (final DataMessage mess: limit(messagesset, limitstart, limitend)) {
			retval.add(mess);
		}
		return retval;
	}

	public void updateDataMessage(Date originalTimestamp, DataMessage newMessage){
		//Find the message by timestamp
		Query query = getDB().query();
		query.constrain(DataMessage.class);
		query.descend("receivedTimestamp").constrain(originalTimestamp);
		ObjectSet<DataMessage> os = query.execute();
		
		if(os.size() == 0)
			throw new IllegalArgumentException("Trying to update a message that does not exist");
		if(os.size() >1)
			throw new IllegalArgumentException("More than one message found to be updated");
		//Delete it
		DataMessage oldmessage = os.get(0);
		getDB().delete(oldmessage);
		//Save new
		getDB().store(newMessage);
		getDB().commit();
	}

	@Override
	public ArrayList<Node> getDevices() throws DataBaseException {
		ArrayList<Node> retval = new ArrayList<Node>();

		Query query = getDB().query();
		query.constrain(Node.class);
		query.descend("lastContact").orderDescending();
		try{
			ObjectSet<Node> obset = query.execute();

			for (final Node dev: obset) {
				retval.add(dev);
			}
		} catch(Exception ex){
			//Just return an empty list
		}
		return retval;
	}

	@Override
	public void updateDevice(Node dev) throws DataBaseException {
		//Find the device by address
		Query query = getDB().query();
		query.constrain(Node.class);
		query.descend("address").constrain(dev.getAddress());
		ObjectSet<Node> os = query.execute();
		if(os.size() == 0)
			throw new IllegalArgumentException("Trying to update a device with address "+dev.getAddress()+" that does not exist");
		Node existingdev = os.get(0);
		//update it
		existingdev.setLocation(dev.getLocation());
		existingdev.setName(dev.getName());
		getDB().store(existingdev);
		getDB().commit();
	}

	@Override
	public ArrayList<Notification> getUnfixedNotifications()
			throws DataBaseException {
		ArrayList<Notification> retval = new ArrayList<Notification>();

		Query query = getDB().query();
		query.constrain(Notification.class);
		query.descend("created").orderDescending();
		query.descend("fixed").constrain(false);
		try{
			ObjectSet<Notification> obset = query.execute();
			for (final Notification dev: obset) {
				retval.add(dev);
			}
		}
		catch(Exception ex){
			//Don't do anything, return an empty list
		}

		return retval;
	}

}
