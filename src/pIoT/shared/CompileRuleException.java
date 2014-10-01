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
 * Thrown if a {@link Rule}s expression cannot be compiled when the rule is 
 * added to the engine.
 */
public class CompileRuleException extends Exception {

	public CompileRuleException(){
		super();
	}
	
	public CompileRuleException(String msg){
		super(msg);
	}

}
