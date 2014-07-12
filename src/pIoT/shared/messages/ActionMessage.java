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
public class ActionMessage implements Serializable, Reflectable {

	int destAddress;
	
	public ActionMessage() {
	}
	
	public ActionMessage(int destination) {
		destAddress = destination;
	}

	public int getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(int destAddress) {
		this.destAddress = destAddress;
	}

}
