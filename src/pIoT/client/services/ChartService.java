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
 * Package containing the definition of the client/server services.
 */
package pIoT.client.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import pIoT.shared.ChartConfiguration;
import pIoT.shared.ChartValue;
import pIoT.shared.DataBaseException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * A service that offers tools for charts.
 * @author Dario Salvi
 *
 */
@RemoteServiceRelativePath("charts")
public interface ChartService extends RemoteService  {

	/**
	 * Retrieves all the data and creates a data table.
	 * @param cf the configuration of the chart
	 * @return a filled table with the timestamp as key and a list
	 * of objects, one per dataset, as columns
	 */
	LinkedHashMap<Long, ArrayList<ChartValue>> getData(ChartConfiguration cf) throws DataBaseException, IllegalArgumentException;
	
	/**
	 * Gives the configuration of the charts defined by the user to be 
	 * shown in home page.
	 * @return an array of {@link ChartConfiguration}
	 * @throws DataBaseException
	 */
	ArrayList<ChartConfiguration> getChartsConfiguration() throws DataBaseException;
	
	void addChartConfiguration(ChartConfiguration cf) throws DataBaseException;
	
	void updateChartConfiguration(ChartConfiguration oldcf, ChartConfiguration newcf) throws DataBaseException;
	
	void removeChartConfiguration(ChartConfiguration cf) throws DataBaseException;
}
