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

import org.dt.reflector.client.Reflectable;

/**
 * A container of values for charts.
 * It has to specify the type contained, which helps then creating the
 * data set for the graph.
 * @author Dario Salvi
 *
 */
public class ChartValue implements Serializable, Reflectable {

	private long timestamp;
	
	private boolean isNull;
	public enum Type {number, date, string, bool};
	private Type type;
	private String stringValue;
	private Double numberValue;
	private Date dateValue;
	private Boolean boolvalue;
	
	public ChartValue() {
		
	}
	
	public ChartValue(long ts, Type t) {
		timestamp = ts;
		isNull = true;
		type = t;
	}
	
	public ChartValue(long ts, String val) {
		timestamp = ts;
		isNull = false;
		stringValue = val;
		type = Type.string;
	}
	
	public ChartValue(long ts, Double val) {
		timestamp = ts;
		isNull = false;
		numberValue = val;
		type = Type.number;
	}
	
	public ChartValue(long ts, Date val) {
		timestamp = ts;
		isNull = false;
		dateValue = val;
		type = Type.date;
	}
	
	public ChartValue(long ts, Boolean val) {
		timestamp = ts;
		isNull = false;
		boolvalue = val;
		type = Type.bool;
	}
	
	public long getTimeStamp(){
		return timestamp;
	}
	
	public boolean isNull(){
		return isNull;
	}

	public Type getType(){
		return type;
	}
	
	public Double getNumber(){
		return numberValue;
	}
	
	public String getString(){
		return stringValue;
	}
	
	public boolean getBoolean(){
		return boolvalue;
	}
	
	public Date getDateTime(){
		return dateValue;
	}
}
