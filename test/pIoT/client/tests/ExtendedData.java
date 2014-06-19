package pIoT.client.tests;

import java.util.ArrayList;

import org.dt.reflector.client.Reflectable;

import pIoT.shared.ReflectableList;

public class ExtendedData implements Reflectable{
	private boolean aBool;
	private int[] anArray;
	private ReflectableList aList = new ReflectableList();
	
	public ExtendedData(){
		
	}
	
	public boolean isABool() {
		return aBool;
	}

	public void setABool(boolean aBool) {
		this.aBool = aBool;
	}

	public ReflectableList getAList() {
		return aList;
	}

	public void setAList(ReflectableList aList) {
		this.aList = aList;
	}

	public int[] getAnArray() {
		return anArray;
	}
	public void setAnArray(int[] anArray) {
		this.anArray = anArray;
	}
	
}