/**
 * 
 */
package pIoT.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import jssc.SerialPortException;

import com.db4o.ObjectSet;
import com.google.gson.Gson;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pIoT.client.services.ActionsService;
import pIoT.shared.DataBaseException;
import pIoT.shared.messages.ActionMessage;
import pIoT.shared.messages.ExtendedActionMessage;
import pIoT.shared.notifications.Notification;

/**
 * Implementation of the {@link ActionsService}
 * @author Dario Salvi
 *
 */
public class ActionsServiceImpl extends RemoteServiceServlet implements ActionsService {

	
	public ActionsServiceImpl() {
	}
	
	@Override
	public ArrayList<ActionMessage> getActionMessageExamples() {
		ArrayList<ActionMessage> examples = new ArrayList<ActionMessage>();
		//ADD HERE EXAMPLES
		
		ArrayList<Float> list = new ArrayList<>();
		list.add(4.5F);
		list.add(6.7F);
		examples.add(new ExtendedActionMessage(10, true, list));
		
		return examples;
	}

	@Override
	public void sendNotification(Notification not) {
		storeNotification(not);
	}

	public static void storeNotification(Notification not){
		//Construct an example by removing the timestamp
		Date originaldate = not.getCreated();
		not.setCreated(null);
		if(DBServiceImpl.getDB().queryByExample(not).size() ==0){
			//No similar notification, then set the timestamp back and save
			not.setCreated(originaldate);
			DBServiceImpl.getDB().store(not);
		}
	}

	@Override
	public void fixNotification(Notification n) throws DataBaseException {
		//Find similar notification
		n.setFixed(false);
		ObjectSet<Notification> os = DBServiceImpl.getDB().queryByExample(n);
		if(os.size() == 0)
			throw new IllegalArgumentException("Trying to fix a notification that does not exist or is already fixed");
		//Update it
		Notification existingn = os.get(0);
		existingn.setFixed(true);
		DBServiceImpl.getDB().store(existingn);
		DBServiceImpl.getDB().commit();
	}

	@Override
	public void sendMessage(ActionMessage mess) throws pIoT.shared.SerialPortException {
		System.out.println("sending action message "+mess);
		
		if(SerialServiceImpl.getPort().isOpened()){
			Gson gson = new Gson();
			String jsonmess = gson.toJson(mess);
			try {
				System.out.println("Sending message "+jsonmess);
				SerialServiceImpl.getPort().writeString(jsonmess);
			} catch (SerialPortException e) {
				throw new pIoT.shared.SerialPortException(e);
			}
		} else throw new pIoT.shared.SerialPortException("Serial port not opened");
	}

}
