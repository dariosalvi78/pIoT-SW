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
 * Package containing server side functionalities.
 */
package pIoT.server;

import static pIoT.server.QueryUtils.limit;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pIoT.client.services.RulesService;
import pIoT.server.SerialServiceImpl.DataHandler;
import pIoT.server.rules.AbstractAction;
import pIoT.server.rules.Engine;
import pIoT.server.rules.PreParser;
import pIoT.server.rules.PreParser.PreParsed;
import pIoT.server.rules.PreParser.PreParsed.Constraint;
import pIoT.shared.CompileRuleException;
import pIoT.shared.DataBaseException;
import pIoT.shared.DuplicateRuleException;
import pIoT.shared.ParseRuleException;
import pIoT.shared.Rule;
import pIoT.shared.messages.DataMessage;
import pIoT.shared.messages.examples.Hello;
import pIoT.shared.messages.examples.LightState;
import pIoT.shared.messages.examples.SwitchSet;
import pIoT.shared.messages.examples.SwitchState;
import pIoT.shared.notifications.Notification;

/**
 * @author Dario Salvi
 *
 */
public class RulesServiceImpl extends RemoteServiceServlet implements RulesService{

	private static final Logger log = Logger.getLogger(RulesServiceImpl.class.getName());

	public static final String lastMessageVarName = "lastMessage";

	private Engine engine;
	private ArrayList<AbstractAction> actions = new ArrayList<>();;
	private TreeMap<String, PreParsed> variables = new TreeMap<>();
	private HashMap<String, Rule> rulesVars = new HashMap<>();
	private ArrayList<Rule> otherRules = new ArrayList<Rule>();
	private int variableCounter;

	private static ArrayList<Class<?>> supportedClasses = new ArrayList<>();

	public RulesServiceImpl() throws DataBaseException, DuplicateRuleException, CompileRuleException, ParseRuleException {
		//KNOWN ACTIONS
		actions.add(new AbstractAction("SENDNOTIFICATION") {

			@Override
			public void execute(Collection<Object>input, String parameters) {
				//substitute know words
				//$SOURCENODE
				if(parameters.contains("$SOURCEADDRESS")){
					if((input != null) && (input.size() >0)){
						//get first input
						int srcaddr = ((DataMessage) input.iterator().next()).getSourceAddress();
						//substitute in params
						parameters = parameters.replace("$SOURCEADDRESS", ""+srcaddr);
					}
				}
				Notification not = new Notification(parameters, Calendar.getInstance().getTime(), false);
				ActionsServiceImpl.storeNotification(not);
			}
		});

		//SUPPORTED CLASSES
		addSupportedClass(DataMessage.class);
		addSupportedClass(Hello.class);
		addSupportedClass(SwitchState.class);
		addSupportedClass(SwitchSet.class);
		addSupportedClass(LightState.class);


		//load static rules
		new StaticRules();

		//start engine
		engine = new Engine();
		//load all rules from DB
		log.fine("Loading rules from DB");
		Query query = DBServiceImpl.getDB().query();
		query.constrain(Rule.class);
		ObjectSet<Rule> obset = query.execute(); 
		for(Rule r : obset){
			parseRule(r);
		}
		variableCounter = 0;

		//register to data
		SerialServiceImpl.addDataHandler(new DataHandler() {
			@Override
			public void handle(DataMessage m) throws Exception {
				reason(m);
			}
		});
		log.info("Rules engine started");
	}

	@Override
	public void reason(DataMessage m) throws DuplicateRuleException {
		//fill an holder of variables - data
		TreeMap<String, ArrayList<Object>> data = new TreeMap<>();
		for(String var : variables.keySet()){
			ArrayList<Object> ds = queryPreparsed(variables.get(var));
			data.put(var, ds);
		}

		//generate permutations
		ArrayList<TreeMap<String, Object>> permutations = new ArrayList<>();
		TreeMap<String, Object> curr = new TreeMap<>();
		generatePermutations(data, 0, curr, permutations);

		//execute all possible permutations
		for(TreeMap<String, Object> dd : permutations){
			ArrayList<String> rules = new ArrayList<>();
			//The engine doesn't like TreeMaps
			HashMap<String, Object> ddd = new HashMap<>();
			for(String s : dd.keySet()){
				if(dd.get(s) != null){
					rules.add(rulesVars.get(s).getFullyQualifiedName());
					ddd.put(s, dd.get(s));
				}
			}
			ddd.put(lastMessageVarName, m);
			log.fine("Executing rules with input permutation: " + ddd);
			engine.executeAllActions(null, rules, ddd, actions);
		}
		ArrayList<String> rules = new ArrayList<>();
		for(Rule r: otherRules){
			rules.add(r.getFullyQualifiedName());
		}
		HashMap<String, Object> ddd = new HashMap<>();
		ddd.put(lastMessageVarName, m);
		log.fine("Executing rules with last message "+m);
		engine.executeAllActions(null, rules, ddd, actions);
	}

	public static void addSupportedClass(Class<?> clazz){
		supportedClasses.add(clazz);
	}

	public ArrayList<Rule> getRules() {
		ArrayList<Rule> rules = new ArrayList<Rule>();
		rules.addAll(rulesVars.values());
		rules.addAll(otherRules);
		return rules;
	}

	public ArrayList<String> getActionNames() {
		ArrayList<String> retv = new ArrayList<>();
		for(AbstractAction act : actions)
			retv.add(act.getName());
		return retv;
	}

	@Override
	public void addRule(Rule rule) throws DataBaseException,
			DuplicateRuleException, CompileRuleException, ParseRuleException {
		String expr = rule.getExpression();
		parseRule(rule);
		
		rule.setExpression(expr);
		DBServiceImpl.getDB().store(rule);
		DBServiceImpl.getDB().commit();
		log.info("Rule "+rule.getName()+" added.");
	}

	@Override
	public void updateRule(String oldRuleName, Rule rule)
			throws DataBaseException, DuplicateRuleException,
			CompileRuleException, ParseRuleException {
		Rule remr = new Rule();
		remr.setNamespace(oldRuleName.substring(0, oldRuleName.lastIndexOf(".")));
		remr.setName(oldRuleName.substring(oldRuleName.lastIndexOf(".")+1));
		removeRule(remr);
		addRule(rule);
	}


	private void parseRule(Rule rule) throws CompileRuleException, DuplicateRuleException, ParseRuleException{
		//preparse rule
		List<PreParsed> preps = PreParser.PreParse(rule.getExpression());
		for(PreParsed p : preps){
			String variableName = "input" + variableCounter;
			p.variableName = variableName;
			rulesVars.put(variableName, rule);
			rule.setExpression( rule.getExpression().replace(p.string, variableName) );
			variables.put(variableName, p);
			variableCounter++;
		}
		if(preps.isEmpty())
			otherRules.add(rule);

		engine.addRule(rule);
	}

	public void removeRule(Rule rule){

		for(String var : new ArrayList<String>( rulesVars.keySet() )){
			Rule r = rulesVars.get(var);
			if(r.getFullyQualifiedName().equalsIgnoreCase(rule.getFullyQualifiedName())){
				variables.remove(var);
				rulesVars.remove(var);
			}
		}
		engine.removeRule(rule);

		Rule exrule = new Rule();
		exrule.setName(rule.getName());
		exrule.setNamespace(rule.getNamespace());
		ObjectSet<Rule> os = DBServiceImpl.getDB().queryByExample(exrule);
		for(Rule r : os){
			DBServiceImpl.getDB().delete(r);
			DBServiceImpl.getDB().commit();
		}
	}

	public static void generatePermutations(
			TreeMap<String, ArrayList<Object>> lists,
			int keyIndex, 
			TreeMap<String, Object> current, 
			ArrayList<TreeMap<String, Object>> retval){

		if(keyIndex == lists.size()){
			retval.add(current);
			return;
		}
		ArrayList<String> keys = new ArrayList<String>(lists.keySet());
		String key = keys.get(keyIndex);
		if((lists.get(key)== null) || (lists.get(key).isEmpty())){
			TreeMap<String, Object> c = new TreeMap<>();
			c.putAll(current);
			c.put(key, null);//
			generatePermutations(lists, keyIndex + 1, c, retval);
		} else{
			for(int i = 0; i < lists.get(key).size(); ++i) {
				TreeMap<String, Object> c = new TreeMap<>();
				c.putAll(current);
				c.put(key, lists.get(key).get(i));
				generatePermutations(lists, keyIndex + 1, c, retval);
			}
		}
	}


	public ArrayList<Object> queryPreparsed(final PreParsed p){
		ArrayList<Object> data = new ArrayList<Object>();
		//get data form queries
		Class<?> clazz = null;
		for(Class<?> c : supportedClasses){
			if(p.className.equalsIgnoreCase(c.getSimpleName())){
				clazz = c;
				break;
			}
		}
		if(clazz == null)
			throw new IllegalArgumentException("Class of name "+p.className+" has not been found among registered ones");

		final Class<?> clazz2 = clazz;

		final boolean[] limits = new boolean[2];
		limits[0] = false;
		limits[1] = false;

		Predicate<DataMessage> predicate = new Predicate<DataMessage>() {

			@Override
			public boolean match(DataMessage candidate) {
				if(!clazz2.isInstance(candidate)) return false;
				boolean retv = true;

				for(Constraint c : p.constraints){
					if(c.variableName.equalsIgnoreCase("limit")){
						if(c.value.equalsIgnoreCase("last")){
							limits[0] = true;
						}
						else if(c.value.equalsIgnoreCase("first")){
							limits[1] = true;
						}
					} else{
						Object v = null;
						try {
							for(PropertyDescriptor pd: Introspector.getBeanInfo(clazz2).getPropertyDescriptors()){
								if(c.variableName.equalsIgnoreCase(pd.getName()))
									v = pd.getReadMethod().invoke(candidate);
							}
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
						if(v instanceof Integer){
							int v1 = (int) v;
							int v2 = Integer.parseInt(c.value);
							if(c.operator.equals("<"))
								retv = retv && (v1 < v2);
							if(c.operator.equals(">"))
								retv = retv && (v1 > v2);
							if((c.operator.equals("=")) || (c.operator.equals("==")))
								retv = retv && (v1 == v2);
							if(c.operator.equals(">="))
								retv = retv && (v1 >= v2);
							if(c.operator.equals("<="))
								retv = retv && (v1 <= v2);
						}
						if(v instanceof Float){
							float v1 = (float) v;
							float v2 = Float.parseFloat(c.value);
							if(c.operator.equals("<"))
								retv = retv && (v1 < v2);
							if(c.operator.equals(">"))
								retv = retv && (v1 > v2);
							if((c.operator.equals("=")) || (c.operator.equals("==")))
								retv = retv && (v1 == v2);
							if(c.operator.equals(">="))
								retv = retv && (v1 >= v2);
							if(c.operator.equals("<="))
								retv = retv && (v1 <= v2);
						}
						if(v instanceof Double){
							double v1 = (double) v;
							double v2 = Double.parseDouble(c.value);
							if(c.operator.equals("<"))
								retv = retv && (v1 < v2);
							if(c.operator.equals(">"))
								retv = retv && (v1 > v2);
							if((c.operator.equals("=")) || (c.operator.equals("==")))
								retv = retv && (v1 == v2);
							if(c.operator.equals(">="))
								retv = retv && (v1 >= v2);
							if(c.operator.equals("<="))
								retv = retv && (v1 <= v2);
						}
						if(v instanceof String){
							String v1 = (String) v;
							String[] vv = c.value.split("'");
							String v2 = vv[1];
							if((c.operator.equals("=")) || (c.operator.equals("==")))
								retv = retv && (v1.equals(v2));
						}
						//TODO: hyerarchical descend
					}
				}

				return retv;
			}
		};

		QueryComparator<DataMessage> comparator = new QueryComparator<DataMessage>() {
			public int compare(DataMessage mess1, DataMessage mess2) {
				if(limits[0])
					return mess2.getReceivedTimestamp().compareTo(mess1.getReceivedTimestamp());
				else 
					return mess1.getReceivedTimestamp().compareTo(mess2.getReceivedTimestamp());
			}
		};


		ObjectSet<DataMessage> messagesset = null;
		try{
			messagesset = DBServiceImpl.getDB().query(predicate, comparator);
		}
		catch(NullPointerException ne){
			//simply means there is no data	
		}
		if((messagesset!= null) && ( limits[0] || limits[1] )){
			for (final DataMessage mess: limit(messagesset, 0, 0)) {
				data.add(mess);
			}
		}
		return data;
	}

}
