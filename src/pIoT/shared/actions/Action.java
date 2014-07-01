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
 * Package that contains actions triggered at the server
 * Classes have to be {@link Serializable}
 * and {@link org.dt.reflector.client.Reflectable}
 */
package pIoT.shared.actions;

import java.io.Serializable;

import org.dt.reflector.client.Reflectable;


/**
 * @author Dario Salvi
 *
 */
public class Action implements Serializable, Reflectable{

	/**
	 * 
	 */
	public Action() {
		// TODO Auto-generated constructor stub
	}

}
