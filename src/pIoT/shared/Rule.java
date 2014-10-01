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
package pIoT.shared;

import java.io.Serializable;

import org.dt.reflector.client.Reflectable;

import pIoT.server.rules.Engine;

/**
 * An {@link Engine} contains a list of rules.  The engine 
 * can then be asked to provide the outcome of the Action associated with the best 
 * matching rule (see {@link Engine#executeBestAction(String, Object, Class)}), 
 * or to provide a list of matching rules (see {@link Engine#getMatchingRules(String, Object)}).  
 * Each rule is "evaluated"
 * by using the {@link #expression} supplied in the constructor 
 * {@link #Rule(String, String, Action, Integer, String, String)}.
 * The expression must be valid expression language.  During evaluation, 
 * the input data is queried in the database using the following syntax:
 * ClassName( query constraints ) and then used in the rules.
 * For example, consider the following rule:<br>
 * <br>
 * <code>Person().name == "ant" && Person().gender == "male"</code><br>
 * <br>
 * Person is the name of the class to be loaded. The query constraints are empty so
 * they won't be used here, meaning that all instances of Person will be loaded.
 * Note how the rule evaluates to "true" or "false".  For any rule to be a candidate
 * to have its {@link Action} executed, it must evaluate to "true".  The rule in the 
 * example above requires an input object which conforms to the bean specification, and 
 * which has attributes"name" and "gender". 
 * If an input object is supplied to the engine such that this rule
 * evaluates to true, then it is a candidate to have its action run, either by 
 * the engine, when {@link Engine#executeBestAction(String, Object, Class)} is called, 
 * or by the application, when {@link Engine#getMatchingRules(String, Object)}
 * returns the rule and the application decides to run the action.<br>
 * <br>
 * Parameters of the rule are a string passed to the action, for example if an action is called
 * "sendMessage", the parameters can be the actual message. It's up to the action
 * to interpret the parameters correctly.
 * <br>
 * Rules belong to namespaces, so that a single engine can be used to evaluate rules 
 * from different components within an application.  Within a namespace, all rules must
 * have unique names (this is checked when adding rules to an engine).  The reason is that
 * rules can be composed of {@link SubRule}s and when they are composed in that manner, 
 * rules refer to subrules by their names.<br>
 * <br>
 * A rule has a priority too.  In certain cases, where the application only requires the 
 * best rule to be used, the priority helps the application decide which rule to run 
 * when there is more than one match.<br>
 * <br>
 * The description in a rule is imply to aid in rule management.<br>
 * <br>
 * For more info on expression language, see {@link http://mvel.codehaus.org/Language+Guide+for+2.0}.
 * 
 * @param name The name of the rule.  Should be unique within the namespace (tested when adding rules to the {@link Engine}).
 * @param expression the rule expressed in expression language. all variables must come from the bean called "input".  The rule MUST evaluate to "true" if it is to be a candidate for execution.
 * @param outcome The name of an action to run, if this rule is the winner.
 * @param priority The priority, used in determining which rule to run, if many evaluate true.  The higher the value, the higher the priority.
 * @param namespace A namespace, used for filtering rules.  The engine is passed a regular expression which is compared to this value.  Only matches are evaluated.
 * @param description A description to help manage rules.
 * @see SubRule
*  */
public class Rule implements Comparable<Rule>, Serializable, Reflectable {

    private String name;
    private String expression;
	private String outcome;
    private String parameters;
    private int priority;
    private String namespace;
    private String description;
    
    public Rule(){
    	
    }
    
    public Rule(final String name, final String expression, final String outcome, final String parameters,
    		final int priority, final String namespace, final String description) {

        if(name == null) throw new AssertionError("name may not be null");
        if(expression == null) throw new AssertionError("expression may not be null");
        if(namespace == null) throw new AssertionError("namespace may not be null");
        
        this.name = name;
        this.expression = expression;
        this.outcome = outcome;
        this.parameters = parameters;
        this.priority = priority;
        this.namespace = namespace;
        this.description = description;
    }
    
    @Override
    public int compareTo(Rule r) {
        //reversed, since we want highest priority first in the list!
        return (this.priority < r.priority ? 1 : (this.priority == r.priority ? 0 : -1));
	}

	/**
	 * @return the {@link #namespace} concatenated with the {@link #name}, separated by a '.'
	 */
    public String getFullyQualifiedName(){
        return namespace + "." + name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((expression == null) ? 0 : expression.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((outcome == null) ? 0 : outcome.hashCode());
        result = prime * result + priority;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Rule other = (Rule) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (expression == null) {
            if (other.expression != null)
                return false;
        } else if (!expression.equals(other.expression))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        if (outcome == null) {
            if (other.outcome != null)
                return false;
        } else if (!outcome.equals(other.outcome))
            return false;
        if (priority != other.priority)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Rule [name=" + name + ", expression=" + expression
                + ", outcome=" + outcome + ", priority=" + priority
                + ", namespace=" + namespace + ", description=" + description
                + "]";
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }

    public String getOutcome() {
        return outcome;
    }
    
    public String getParameters(){
    	return parameters;
    }

    public int getPriority() {
        return priority;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getDescription() {
        return description;
    }
    
    public void setName(String name) {
		this.name = name;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
