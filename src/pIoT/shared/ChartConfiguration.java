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
import java.util.ArrayList;
import java.util.Date;

import org.dt.reflector.client.Reflectable;

/**
 * Configuration of a chart.
 * @author Dario Salvi
 *
 */
public class ChartConfiguration implements Serializable, Reflectable {
	
	public static enum ChartType { LINES };
	
	private ChartType type;
	private String title;
	private String valuesName;
	private Date startDate;
	private Date endDate;
	private ArrayList<DataSetConfiguration> datasets;

	public ChartConfiguration() {
		// Empty constructor
		datasets = new ArrayList<>();
	}

	public ChartType getType() {
		return type;
	}

	public void setType(ChartType type) {
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValuesName() {
		return valuesName;
	}

	public void setValuesName(String valuesName) {
		this.valuesName = valuesName;
	}

	public ArrayList<DataSetConfiguration> getDatasets() {
		return datasets;
	}

	public void setDatasets(ArrayList<DataSetConfiguration> datasets) {
		this.datasets = datasets;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
