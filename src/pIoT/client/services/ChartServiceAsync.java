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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async counter part of {@link ChartService}
 * @author Dario Salvi
 *
 */
public interface ChartServiceAsync {

	void getData(ChartConfiguration cf, AsyncCallback<LinkedHashMap<Long, ArrayList<ChartValue>>> callback);
	
	void getChartsConfiguration(AsyncCallback<ArrayList<ChartConfiguration>> callback);

	void addChartConfiguration(ChartConfiguration cf, AsyncCallback<Void> callback);
	
	void updateChartConfiguration(ChartConfiguration oldcf, ChartConfiguration newcf, AsyncCallback<Void> callback);
	
	void removeChartConfiguration(ChartConfiguration cf, AsyncCallback<Void> callback);

}
