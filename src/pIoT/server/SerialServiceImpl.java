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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import pIoT.client.services.SerialService;
import pIoT.shared.Node;
import pIoT.shared.SerialPortException;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.messages.examples.Error;
import pIoT.shared.messages.examples.Hello;
import pIoT.shared.messages.examples.LightState;
import pIoT.shared.messages.examples.SwitchState;
import pIoT.shared.notifications.NewDeviceNotification;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortList;

import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
public class SerialServiceImpl extends RemoteServiceServlet implements SerialService {

	private static Logger logger = Logger.getLogger(SerialServiceImpl.class.getName());

	private static SerialPort port;
	private String portName;
	private String stringBuffer = "";
	private long lastRead = 0;

	public SerialServiceImpl(){
		//ADD HERE DATA MESSAGE CLASSES
		ObjectParser.addClassType(DataMessage.class);
		//Classes of the example sketches:
		ObjectParser.addClassType(Hello.class);
		ObjectParser.addClassType(LightState.class);
		ObjectParser.addClassType(SwitchState.class);
		ObjectParser.addClassType(Error.class);


		portName = Configs.retrieveConfigs().getComPort();

		if(portName == null){
			//Pick a random one
			String[] ports = SerialPortList.getPortNames();
			if((ports != null) && (ports.length >0))
				portName = ports[0];
		}

		port = new SerialPort(portName);
		logger.info("Serial Service started");
	}

	public static SerialPort getPort(){
		return port;
	}

	@Override
	public ArrayList<String> getComPorts() throws IllegalArgumentException {
		ArrayList<String> res = new ArrayList<>();
		String[] ports = SerialPortList.getPortNames();
		String logmessage = "Available com ports: ";
		for(int i=0; i<ports.length; i++){
			res.add(ports[i]);
			logmessage +=ports[i]+" ";
		}
		logger.info(logmessage);
		return res;
	}

	@Override
	public String getCurrentComPort() {
		return portName;
	}

	@Override
	public boolean startStop(String portname) throws SerialPortException {
		try {
			if(port.isOpened()){
				logger.info("Com port "+port.getPortName()+" stopped");
				stop();
				return false;
			} else {
				logger.info("Com port "+port.getPortName()+" started");
				start(portname);
				return true;
			}
		} catch (jssc.SerialPortException e) {
			logger.log(Level.SEVERE, "Cannot start/stop com port "+port.getPortName(), e);
			throw new SerialPortException(e.getMessage());
		}
	}

	@Override
	public boolean isStarted() {
		return port.isOpened();
	}

	private void start(String portname) throws jssc.SerialPortException {
		this.portName = portname;
		port = new SerialPort(portname);
		port.openPort();
		port.setParams(57600, 8, 1, 0);

		//Save the portname for future reference
		try{
			Configs cfs = Configs.retrieveConfigs();
			cfs.setComPort(portname);
			DBServiceImpl.getDB().store(cfs);
		} catch(Exception e){
			//Nothing to do here
		}
		port.addEventListener(new SerialPortEventListener() {
			@Override
			public void serialEvent(SerialPortEvent arg0) {
				if(arg0.isRXCHAR()){
					try {
						String str = port.readString();
						if(str != null){
							logger.info("Com port says: "+str);
							//Accumulate only if requests are newer than 10 seconds
							//otherwise just rewrite the string
							long now = Calendar.getInstance().getTimeInMillis();
							if((now - lastRead) > 10000)
								stringBuffer = str; 
							else stringBuffer += str;

							Object o = ObjectParser.parse(str);
							while(o!= null){
								if(o instanceof DataMessage){
									logger.info("Object parsed from serial com of class " + o.getClass().getName());
									DataMessage m = (DataMessage) o;
									//set missing fields
									m.setReceivedTimestamp(Calendar.getInstance().getTime());
									m.setSourceMessage(ObjectParser.getParsedMessage());
									//store it !
									DBServiceImpl.store(m);
									logger.finer("Object stored");
									//update devices
									int devAddr = m.getSourceAddress();
									if(devAddr!= 0){
										manageDevice(devAddr);
									}
								}
								else if(o instanceof Error){
									Error er = (Error) o;
									if(er.getSeverity() == 0)
										logger.info(er.getMessage());
									else if(er.getSeverity() == 1)
										logger.warning(er.getMessage());
									else logger.severe(er.getMessage());
								}
								else{ logger.severe("Object parsed from serial com is of class "
								+o.getClass().getName()+" that is not a DataMessage");}
								
								//re-parse, maybe there's still something in there
								o = ObjectParser.parse("");
							}
						}

					} catch (jssc.SerialPortException e) {
						//Nothing to do here
						logger.log(Level.WARNING, "Error while reading serial event", e);
					}
				}
			}
		});
	}

	private void stop() throws jssc.SerialPortException{
		port.closePort();
	}

	@Override
	public String getBuffer() {
		lastRead = Calendar.getInstance().getTimeInMillis();
		return stringBuffer;
	}

	@Override
	public void sendData(String data) throws SerialPortException {
		try {
			logger.info("Sending data on com port: "+data);
			port.writeString(data);
		} catch (jssc.SerialPortException e) {
			logger.log(Level.WARNING, "Error while sending data on com port", e);
			throw new SerialPortException(e.getMessage());
		}
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