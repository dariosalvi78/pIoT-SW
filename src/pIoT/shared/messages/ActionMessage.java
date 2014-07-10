/**
 * 
 */
package pIoT.shared.messages;

import java.io.Serializable;

import org.dt.reflector.client.Reflectable;

/**
 * A message sent to nodes that defines an action.
 * @author Dario Salvi
 *
 */
public abstract class ActionMessage implements Serializable, Reflectable {

	int destAddress;
	
	/**
	 * 
	 */
	public ActionMessage() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract ActionMessage getExample();

}
