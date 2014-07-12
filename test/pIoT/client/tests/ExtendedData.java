package pIoT.client.tests;

import java.io.Serializable;
import java.util.ArrayList;

import org.dt.reflector.client.Reflectable;


public class ExtendedData implements Serializable, Reflectable{
	private boolean aBool;
	private int[] anArray;
	private ArrayList<Integer> aList = new ArrayList<Integer>();
	
	public ExtendedData(){
	}
	
	public ExtendedData(boolean aBool, int[] anArray, ArrayList<Integer> aList) {
		super();
		this.aBool = aBool;
		this.anArray = anArray;
		this.aList = aList;
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
	
	public String toString(){
		return "bool: "+aBool+" list: "+aList+" array: "+anArray;
	}
	
}