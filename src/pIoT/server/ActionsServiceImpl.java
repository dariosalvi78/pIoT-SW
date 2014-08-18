/**
 * 
 */
package pIoT.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import jssc.SerialPortException;

import com.db4o.ObjectSet;
import com.google.gson.Gson;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pIoT.client.services.ActionsService;
import pIoT.shared.DataBaseException;
import pIoT.shared.messages.ActionMessage;
import pIoT.shared.messages.examples.SwitchSet;
import pIoT.shared.notifications.Notification;

/**
 * Implementation of the {@link ActionsService}
 * @author Dario Salvi
 *
 */
public class ActionsServiceImpl extends RemoteServiceServlet implements ActionsService {

	private static Logger logger = Logger.getLogger(ActionsServiceImpl.class.getName());
	
	private static ArrayList<ActionMessage> exampleActionMessages = new ArrayList<ActionMessage>();

	public ActionsServiceImpl() {
		//ADD HERE EXAMPLES
		exampleActionMessages.add(new SwitchSet(10, true));
		
		logger.info("Action service started");
	}

	@Override
	public ArrayList<ActionMessage> getActionMessageExamples() {
		return exampleActionMessages;
	}

	@Override
	public void sendNotification(Notification not) {
		storeNotification(not);
		logger.info("Notification stored: "+not.getMessage());
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
		logger.info("Notification fixed: "+n.getMessage());
	}

	@Override
	public void sendMessage(ActionMessage mess) throws pIoT.shared.SerialPortException {
		logger.info("Sending action message "+mess);

		if(SerialServiceImpl.getPort().isOpened()){
			Gson gson = new Gson();
			String jsonmess = "{ \""+mess.getClass().getSimpleName()+"\": ";
			jsonmess+= gson.toJson(mess);
			jsonmess += " }";
			try {
				SerialServiceImpl.getPort().writeString(jsonmess);
			} catch (SerialPortException e) {
				throw new pIoT.shared.SerialPortException(e);
			}
		} else throw new pIoT.shared.SerialPortException("Serial port not opened");
	}

}
