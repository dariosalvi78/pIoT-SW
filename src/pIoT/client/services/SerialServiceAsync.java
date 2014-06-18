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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>SerialService</code>.
 */
public interface SerialServiceAsync {
	void getComPorts(AsyncCallback<ArrayList<String>> callback);
	
	void getCurrentComPort(AsyncCallback<String> callback);
	
	void startStop(String portname, AsyncCallback<Boolean> callback);
	
	void isStarted(AsyncCallback<Boolean> callback);
	
	void getBuffer(AsyncCallback<String> callback);
	
	void sendData(String data, AsyncCallback<Void> callback);
}
