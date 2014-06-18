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
import java.util.Date;

/**
 * Representation of a device.
 * @author Dario Salvi
 *
 */
@SuppressWarnings("serial")
public class Node implements Serializable{

	/**
	 * The address of the device.
	 * pIoT devices support 4 bytes addresses, java integers are made of 4 bytes.
	 */
	private int address;

	/**
	 * A human understandable name.
	 */
	private String name;

	/**
	 * A human readable location.
	 */
	private String location;

	/**
	 * Last time the device sent data.
	 */
	private Date lastContact;

	public Node() {
	}

	public Node(int address, String name, String location) {
		super();
		this.address = address;
		this.name = name;
		this.location = location;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getLastContact() {
		return lastContact;
	}

	public void setLastContact(Date lastContact) {
		this.lastContact = lastContact;
	}

}
