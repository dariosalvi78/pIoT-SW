/**
 * 
 */
package pIoT.client.tests;

import java.util.ArrayList;

import pIoT.shared.messages.ActionMessage;

/**
 * @author Dario
 *
 */
public class ExtendedActionMessage extends ActionMessage {

	boolean aBool;
	ArrayList<Float> aList;
	
	
	public ExtendedActionMessage() {
	}

	public ExtendedActionMessage(int destination, boolean aBool, ArrayList<Float> aList) {
		super(destination);
		this.aBool = aBool;
		this.aList = aList;
	}
	
	public boolean isABool() {
		return aBool;
	}

	public void setABool(boolean aBool) {
		this.aBool = aBool;
	}
	

	public ArrayList<Float> getAList() {
		return aList;
	}

	public void setAList(ArrayList<Float> aList) {
		this.aList = aList;
	}

	
	public String toString(){
		return "ext action addr: "+getDestAddress()+", bool: "+aBool+", list: "+aList;
	}
}
