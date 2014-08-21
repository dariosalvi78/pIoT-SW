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
package pIoT.client.tests;

import java.util.Date;

import pIoT.shared.messages.DataMessage;


/**
 * An example extension of the DataMessage, used for testing purposes
 * @author Dario Salvi
 *
 */
public class ExtendedDataMessage extends DataMessage {

	private String extendedMessage;
	
	private ExtendedData data;
	
	public static class NestedData{
		private String nest;
		public NestedData() {
		}
		public String getNest() {
			return nest;
		}
		public void setNest(String nest) {
			this.nest = nest;
		}
	}
	
	private NestedData nested;
	
	public ExtendedDataMessage() {
	}

	public ExtendedDataMessage(Date receivedTimestamp, String sourceMessage,
			int sourceaddress, String extendedMessage, ExtendedData data, NestedData nested) {
		super(receivedTimestamp, sourceMessage, sourceaddress);
		this.extendedMessage = extendedMessage;
		this.data = data;
		this.nested = nested;
	}

	public String getExtendedMessage() {
		return extendedMessage;
	}

	public void setExtendedMessage(String extendedMessage) {
		this.extendedMessage = extendedMessage;
	}
	
	public ExtendedData getData() {
		return data;
	}

	public void setData(ExtendedData data) {
		this.data = data;
	}
	
	public NestedData getNested() {
		return nested;
	}

	public void setNested(NestedData nested) {
		this.nested = nested;
	}

	public String toString(){
		return "ext message: "+ extendedMessage+" data: "+ data.toString()
				+ " nested: "+ nested.nest;
	}
}
