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
 * Package containing shared objects between server and client.
 * All these classes have to be serializable.
 */
package pIoT.shared;

import java.io.Serializable;

/**
 * An exception caused by the DataBase.
 * @author Dario Salvi
 *
 */
public class DataBaseException extends Exception implements Serializable{
	
	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = -8463575425395162135L;
	
	public DataBaseException() {
		super();
	}

	public DataBaseException(String arg0) {
		super(arg0);
	}

	public DataBaseException(Throwable arg0) {
		super(arg0);
	}
	public DataBaseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
