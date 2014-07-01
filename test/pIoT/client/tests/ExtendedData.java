package pIoT.client.tests;

import java.util.ArrayList;

import org.dt.reflector.client.Reflectable;


public class ExtendedData implements Reflectable{
	private boolean aBool;
	private int[] anArray;
	private ArrayList<Integer> aList = new ArrayList<Integer>();
	
	public ExtendedData(){
		
	}
	
	public boolean isABool() {
		return aBool;
	}

	public void setABool(boolean aBool) {
		this.aBool = aBool;
	}

	public ArrayList<Integer> getAList() {
		return aList;
	}

	public void setAList(ArrayList<Integer> aList) {
		this.aList = aList;
	}

	public int[] getAnArray() {
		return anArray;
	}
	public void setAnArray(int[] anArray) {
		this.anArray = anArray;
	}
	
}