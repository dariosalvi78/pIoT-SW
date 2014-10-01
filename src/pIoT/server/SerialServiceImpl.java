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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import pIoT.client.services.SerialService;
import pIoT.shared.SerialPortException;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.messages.examples.Error;
import pIoT.shared.messages.examples.Hello;
import pIoT.shared.messages.examples.LightState;
import pIoT.shared.messages.examples.SwitchState;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
public class SerialServiceImpl extends RemoteServiceServlet implements SerialService {

	private static Logger logger = Logger.getLogger(SerialServiceImpl.class.getName());
	private static String serialLogFileName = "SerialLog.log";
	private BufferedWriter serialWriter;
	
	private static SerialPort port;
	private String portName;
	private String stringBuffer = "";
	private long lastRead = 0;
	
	/**
	 * A generic handler of new arrived data.
	 */
	public interface DataHandler{
		public void handle(DataMessage m) throws Exception;
	}
	
	private static ArrayList<DataHandler> handlers = new ArrayList<>();

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
		try {
			serialWriter = new BufferedWriter(new FileWriter(new File(serialLogFileName)));
		} catch (IOException e) {
			logger.log(Level.WARNING, "Cannot start serial logging", e);
		}
		
		logger.info("Serial Service started");
	}

	@Override
	public void destroy(){
		try {
			serialWriter.close();
		} catch (IOException e) {
		}
		super.destroy();
	}
	
	public static void addDataHandler(DataHandler h){
		handlers.add(h);
	}
	
	public static void removeHandler(DataHandler h){
		handlers.remove(h);
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
							try {
								serialWriter.write(str);
							} catch (IOException e) {
								logger.log(Level.WARNING, "Cannot write serial log", e);
							}
							//Accumulate only if requests are newer than 10 seconds
							//otherwise just rewrite the string
							long now = Calendar.getInstance().getTimeInMillis();
							if((now - lastRead) > 10000)
								stringBuffer = str; 
							else stringBuffer += str;

							Object o = ObjectParser.parse(str);
							while(o!= null){
								if(o instanceof DataMessage){
									logger.fine("Object parsed from serial com of class " + o.getClass().getName());
									DataMessage m = (DataMessage) o;
									//set missing fields
									m.setReceivedTimestamp(Calendar.getInstance().getTime());
									m.setSourceMessage(ObjectParser.getParsedMessage());
									//store it !
									DBServiceImpl.store(m);
									//call handlers
									for(DataHandler h : handlers){
										try {
											h.handle(m);
										} catch (Exception e) {
											logger.log(Level.SEVERE, "Problem while handling data. "+e.getMessage(), e);;
										}
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
			logger.fine("Sending data on com port: "+data);
			port.writeString(data);
		} catch (jssc.SerialPortException e) {
			logger.log(Level.WARNING, "Error while sending data on com port", e);
			throw new SerialPortException(e.getMessage());
		}
	}

}