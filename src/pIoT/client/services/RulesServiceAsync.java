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

import com.google.gwt.user.client.rpc.AsyncCallback;

import pIoT.shared.Rule;

/**
 * The async counterpart of {@link RulesService}
 * @author Dario Salvi
 *
 */
public interface RulesServiceAsync {

	public void getRules(AsyncCallback<ArrayList<Rule>> callback);
	
	public void getActionNames(AsyncCallback<ArrayList<String>> callback);
	
	public void saveRule(Rule rule, AsyncCallback<Void> callback);
	
	public void removeRule(Rule rule, AsyncCallback<Void> callback);
	
	public void reason(AsyncCallback<Void> callback);
}
