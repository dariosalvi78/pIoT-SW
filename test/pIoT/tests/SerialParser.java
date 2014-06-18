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
 * Package containing some automatic tests.
 */
package pIoT.tests;


import pIoT.server.ObjectParser;
import pIoT.shared.messages.DataMessage;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialParser {

	public static void main(String[] args) throws SerialPortException{
		ObjectParser.addClassType(DataMessage.class);
		
		final SerialPort port = new SerialPort("COM4");
		port.openPort();
		port.addEventListener(new SerialPortEventListener() {

			@Override
			public void serialEvent(SerialPortEvent arg0) {
				if(arg0.isRXCHAR()){
					try{
						String str = port.readString();
						System.out.println("Got data: " +str);
						Object o = ObjectParser.parse(str);
						if(o != null)
							System.out.println("Parsed object of type " + o.getClass().getName());
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
		});
	}

}
