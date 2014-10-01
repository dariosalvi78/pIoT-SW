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


/**
 * thrown when rules are added to the engine, if another rule
 * has the same fully qualified name.
 * @see Rule#getFullyQualifiedName()
 */
public class DuplicateRuleException extends Exception {

	public DuplicateRuleException() {
		super();
	}
	
	public DuplicateRuleException(String msg) {
		super(msg);
	}

}
