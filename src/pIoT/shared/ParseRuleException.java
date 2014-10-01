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

import pIoT.server.rules.Engine;

/**
 * When replacing {@link SubRule} placeholders (the '#' character) in rules, 
 * or when replacing queries, this exception may
 * be thrown if no suitable rule or query can be found.  
 * 
 * @see The "addRules" methods in {@link Engine}. 
 */
public class ParseRuleException extends Exception {

	public ParseRuleException() {
		super();
	}
	
	public ParseRuleException(String msg) {
		super(msg);
	}

}
