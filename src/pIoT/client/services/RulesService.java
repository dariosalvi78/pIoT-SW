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

import pIoT.shared.CompileRuleException;
import pIoT.shared.DataBaseException;
import pIoT.shared.DuplicateRuleException;
import pIoT.shared.ParseRuleException;
import pIoT.shared.Rule;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Dario Salvi
 *
 */
@RemoteServiceRelativePath("rules")
public interface RulesService extends RemoteService {

	public ArrayList<Rule> getRules();
	
	public ArrayList<String> getActionNames();
	
	public void saveRule(Rule rule) throws DataBaseException, DuplicateRuleException, CompileRuleException, ParseRuleException;
	
	public void removeRule(Rule rule);
	
	public void reason() throws DuplicateRuleException;
}
