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

import pIoT.shared.SerialPortException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the Serial service.
 * 
 * @author Dario Salvi
 */
@RemoteServiceRelativePath("serial")
public interface SerialService extends RemoteService {
	ArrayList<String> getComPorts();
	
	String getCurrentComPort();
	
	boolean isStarted();
	
	boolean startStop(String portname) throws SerialPortException;
	
	String getBuffer();
	
	void sendData(String data) throws SerialPortException;
	
}
