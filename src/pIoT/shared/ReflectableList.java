/**
 * 
 */
package pIoT.shared;

import java.io.Serializable;
import java.util.ArrayList;

import org.dt.reflector.client.Reflectable;

/**
 * @author Dario
 *
 */
public class ReflectableList implements Serializable,
		Reflectable {
	
	ArrayList<Object> list;
	int index;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ReflectableList() {
		super();
		list = new ArrayList();
		index = 0;
	}

	public Object getNext(){
		return list.get(index++);
	}
	
	public void setNext(Object item){
		list.set(index++, item);
	}
	
	
}
