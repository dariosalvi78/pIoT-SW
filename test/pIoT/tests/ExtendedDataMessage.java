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

import java.util.Date;

import pIoT.shared.messages.DataMessage;

import com.gwtent.reflection.client.Reflectable;

/**
 * An example extension of the DataMessage, used for testing purposes
 * @author Dario Salvi
 *
 */
@Reflectable
public class ExtendedDataMessage extends DataMessage {

	public static class ExtendedData{
		private boolean aBool;
		private int[] anArray;
		
		public ExtendedData(){
			
		}
		
		public boolean getABool() {
			return aBool;
		}
		public void setABool(boolean aBool) {
			this.aBool = aBool;
		}
		public int[] getAnArray() {
			return anArray;
		}
		public void setAnArray(int[] anArray) {
			this.anArray = anArray;
		}
		
	}
	
	private String extendedMessage;
	
	private ExtendedData data;
	
	public ExtendedDataMessage() {
	}

	public ExtendedDataMessage(Date receivedTimestamp, String sourceMessage,
			int sourceaddress, String extendedMessage, ExtendedData data) {
		super(receivedTimestamp, sourceMessage, sourceaddress);
		this.extendedMessage = extendedMessage;
		this.data = data;
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
}
