/**
 * Copyright (c) 2011 Ant Kutschera
 * Copyleft 2014 Dario Salvi
 * 
 * This file is an adaptation of the Rule Engine from Ant Kutschera's blog.
 * 
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package pIoT.server.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.mvel2.MVEL;

import pIoT.shared.CompileRuleException;
import pIoT.shared.DuplicateRuleException;
import pIoT.shared.ParseRuleException;
import pIoT.shared.Rule;

/**
 * A Rule Engine.  Can evaluate rules and execute {@link AbstractAction}s or simply provide an 
 * ordered list of the best matching {@link Rule}s.<br>
 * <br>
 * A fuller explanation of how to use rules can be found in the Javadoc for {@link Rule}s.<br>
 * <br>
 * Based on MVEL expression language from Codehaus.<br>
 *
 */
public class Engine {

	private static final Logger log = Logger.getLogger(Engine.class.getName());

	private final List<CompiledRule> rules;
	private final Set<String> uniqueOutcomes;

	public Engine() {
		rules = new ArrayList<CompiledRule>();
		uniqueOutcomes = new HashSet<String>();
	}

	public void addRule(Rule rule) throws CompileRuleException, DuplicateRuleException, ParseRuleException{
		String fullyQualifiedName = rule.getFullyQualifiedName();
		for(CompiledRule r1 : rules)
			if(r1.getRule().getFullyQualifiedName().equalsIgnoreCase(rule.getFullyQualifiedName())){
				throw new DuplicateRuleException("The name " + fullyQualifiedName + 
						" was found in a different rule.");
			}
		uniqueOutcomes.add(rule.getOutcome());

		//now replace all rule references with the actual rule, contained within brackets
		String expression = rule.getExpression();
		int idx1 = expression.indexOf('#');
		int idx2 = 0;

		while(idx1 != -1){
			//search to end of expression for next symbol
			idx2 = idx1 + 1; //to skip #
			while(true){
				idx2++;
				if(idx2 >= expression.length()){
					break;
				}
				char c = expression.charAt(idx2);
				if(
						c == ' ' || c == '&' || 
						c == '|' || c == '.' || 
						c == '(' || c == ')' || 
						c == '[' || c == ']' || 
						c == '{' || c == '}' || 
						c == '+' || c == '-' || 
						c == '/' || c == '*' || 
						c == '=' || c == '!'
						){
					//end of token
					break;
				}
			}

			String token = expression.substring(idx1, idx2);
			String fullyQualifiedRuleRef = rule.getNamespace() + "." 
					+ token.substring(1, token.length());

			Rule toAdd = null;
			for(CompiledRule cr : this.rules){
				if(cr.getRule().getFullyQualifiedName().equalsIgnoreCase(fullyQualifiedRuleRef)){
					toAdd = cr.getRule();
				}
			}
			if(toAdd == null){
				throw new ParseRuleException("Error while attempting to add subrule to rule " + rule.getFullyQualifiedName() +
						".  Unable to replace " + token + " with subrule " + fullyQualifiedRuleRef + 
						" because no subrule with that fully qualified name was found");
			}
			expression = expression.replaceAll(token, "(" + toAdd.getExpression() + ")");
			idx1 = expression.indexOf('#');
		}
		rule.setExpression(expression);

		//precomile
		try{
			this.rules.add(new CompiledRule(rule));
			log.info("added rule: " + rule);
		}catch(org.mvel2.CompileException ex){
			log.warning("Failed to compile " + rule.getFullyQualifiedName() + ": " + ex.getMessage());
			throw new CompileRuleException(ex.getMessage());
		}
	}
	
	public void removeRule(Rule rule){
		String fulName = rule.getFullyQualifiedName();
		CompiledRule toremove = null;
		for(CompiledRule cr : rules){
			if(cr.getRule().getFullyQualifiedName().equalsIgnoreCase(fulName))
				toremove = cr;
		}
		if(toremove != null)
			rules.remove(toremove);
	}

	/**
	 * Evaluates all rules against the input and returns the result 
	 * of the outcome associated with the rule having the highest priority.
	 * @param nameSpacePattern if not null, then only rules with matching namespaces are evaluated
	 * @param input the map containing all inputs to the expression language rule
	 * @return The best rule which is found
	 * Rules must evaluate to true in order to be candidates.
	 */
	public Rule getBestOutcome(String nameSpacePattern, Collection<String> rulesNames, Map<String, Object> input) {

		List<Rule> matches = getMatchingRules(nameSpacePattern, rulesNames, input);
		if(matches == null || matches.isEmpty()){
			return null;
		}else{
			return matches.get(0);
		}
	}


	/**
	 * Evaluates all rules against the input and returns the result
	 * of the action associated with the rule having the highest priority.
	 * @param nameSpacePattern optional.  if not null, then only rules with matching namespaces are evaluated.
	 * @param input the map containing all inputs to the expression language rule.
	 * @param actions a collection of actions containing one action per possible outcome.  The action whose name is equal to the winning outcome will be executed.
	 * @return The result of the {@link AbstractAction} with the same name as the winning rules outcome.
	 * @throws NoMatchingRuleFoundException If no matching rule was found.  Rules must evaluate to true in order to be candidates.
	 * @throws NoActionFoundException If no action with a name matching the winning rules outcome was found.
	 * @throws DuplicateRuleException if any actions have the same name.
	 */
	public void executeBestAction(String nameSpacePattern, Collection<String> rulesNames, Map<String, Object> input, 
			Collection<AbstractAction> actions) throws  DuplicateRuleException {

		Map<String, AbstractAction> actionsMap = validateActions(actions);
		Rule bestrule = getBestOutcome(nameSpacePattern, rulesNames, input);
		actionsMap.get(bestrule.getOutcome()).execute(input.values(), bestrule.getParameters());
	}

	/**
	 * Evaluates all rules against the input and then executes all action 
	 * associated with the positive rules outcomes, in order of highest priority first.<br>
	 * <br>
	 * Any outcome is only ever executed once!<br>
	 * <br>
	 * @param nameSpacePattern optional.  if not null, then only rules with matching namespaces are evaluated.
	 * @param input the map containing all inputs to the expression language rule.
	 * @param actions a collection of actions containing one action per possible outcome.  The actions whose names is equal to the positive outcomes will be executed.
	 * @throws NoMatchingRuleFoundException If no matching rule was found.  Rules must evaluate to true in order to be candidates.
	 * @throws NoActionFoundException If no action with a name matching the winning rules outcome was found.
	 * @throws DuplicateRuleException if any actions have the same name.
	 */
	public void executeAllActions(String nameSpacePattern, Collection<String> rulesNames, Map<String, Object> input, 
			Collection<AbstractAction> actions) throws DuplicateRuleException {

		Map<String, AbstractAction> actionsMap = validateActions(actions);

		List<Rule> matchingRules = getMatchingRules(nameSpacePattern, rulesNames, input);

		if((matchingRules!= null) && (!matchingRules.isEmpty())){
			Set<String> executedOutcomes = new HashSet<String>();
			for(Rule r : matchingRules){
				//only run, if not already run!
				if(!executedOutcomes.contains(r.getOutcome())){

					actionsMap.get(r.getOutcome()).execute(input.values(), r.getParameters());

					executedOutcomes.add(r.getOutcome());
				}
			}
		}
	}

	private Map<String, AbstractAction> validateActions(Collection<AbstractAction> actions) throws DuplicateRuleException{
		//do any actions have duplicate names?
		Map<String, AbstractAction> actionsMap = new HashMap<String, AbstractAction>();
		for(AbstractAction a : actions){
			if(actionsMap.containsKey(a.getName())){
				throw new DuplicateRuleException("The name " + a.getName() + " was found in a different action.  Action names must be unique.");
			}else{
				actionsMap.put(a.getName(), a);
			}
		}

		//do we have at least one action for every possible outcome?  
		//better to test now, rather than in production...
		for(String outcome : uniqueOutcomes){
			if(outcome != null && !actionsMap.containsKey(outcome)){
				return null;
			}
		}

		return actionsMap;
	}


	/**
	 * @param nameSpacePattern if not null, then only rules with matching namespaces are evaluated
	 * @param rulesNames if not null, only evaluates the rules with the names provided in the collection
	 * @param input a map all inputs to the expression language rule
	 * @return an ordered list of Rules which evaluated to "true", sorted by {@link Rule#getPriority()},
	 * with the highest priority rules first in the list.
	 */
	public List<Rule> getMatchingRules(String nameSpacePattern, Collection<String> rulesNames, Map<String, Object> input) {

		Pattern pattern = null;
		if(nameSpacePattern != null){
			pattern = Pattern.compile(nameSpacePattern);
		}

		List<Rule> matchingRules = new ArrayList<Rule>();
		for(CompiledRule r : rules){
			
			if((rulesNames != null) && (!rulesNames.contains(r.getRule().getFullyQualifiedName()))){
				log.finer("Rule "+r.getRule().getFullyQualifiedName()+" discarded");
				continue;
			}

			if(pattern != null){
				if(!pattern.matcher(r.getRule().getNamespace()).matches()){
					log.finer("Rule "+r.getRule().getFullyQualifiedName()+" discarded");
					continue;
				}
			}
			Object o = MVEL.executeExpression(r.getCompiled(), input);
			String msg = r.getRule().getFullyQualifiedName() + "-{" + r.getRule().getExpression() + "}";
			if(String.valueOf(o).equals("true")){
				matchingRules.add(r.getRule());
				log.info("matched: " + msg);
			}else{
				log.info("unmatched: " + msg);
			}
		}

		//order by priority!
		Collections.sort(matchingRules);

		return matchingRules;
	}

	private static final class CompiledRule {

		private Rule rule;

		private Serializable compiled;

		public CompiledRule(Rule rule) {
			this.rule = rule;
			this.compiled = MVEL.compileExpression(rule.getExpression());
		}

		public Serializable getCompiled() {
			return compiled;
		}

		public Rule getRule() {
			return rule;
		}
	}

}

