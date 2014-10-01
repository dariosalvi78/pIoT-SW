/*
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

import java.util.ArrayList;
import java.util.List;

import pIoT.server.rules.PreParser.PreParsed.Constraint;

public class PreParser {
	
	public static class PreParsed{
		public String className;
		public String string;
		public String variableName;
		
		public static class Constraint{
			public String variableName;
			public String operator;
			public String value;
		}
		
		public List<Constraint> constraints;
	}

	private static String varNameExpr = "[a-zA-Z_$][a-zA-Z\\d_$]*";
	private static String spacesExpr = "[ \\t\\n\\r]*";
	private static String valExpr = "[a-zA-Z0-9\\.\\\']*";
	private static String operatorExpr = "[=<>(<=)(>=)]*";

	private static String preparseStuff = varNameExpr+ "\\("+
			"(" + spacesExpr + varNameExpr + spacesExpr + operatorExpr + 
			spacesExpr + valExpr + spacesExpr + "\\,)*"+
			"(" + spacesExpr + varNameExpr + spacesExpr + operatorExpr + 
			spacesExpr + valExpr + spacesExpr + ")*"+
			spacesExpr + "\\)";
	
	public static List<PreParsed> PreParse(String line) {
		ArrayList<PreParsed> retval = new ArrayList<PreParsed>();
		
		String[] mvelStuff = line.split(preparseStuff);
		
		for(String s : mvelStuff){
			PreParsed prep = new PreParsed();
			
			int start = 0;
			if(!s.isEmpty()){
				start = line.indexOf(s)+s.length();
			}
			if(start >= line.length()) break;
			
			String preparsedLine = line.substring(start);
			preparsedLine = preparsedLine.substring(0, preparsedLine.indexOf(")")+1);
			if(!preparsedLine.matches(preparseStuff)) break;
			
			prep.string = preparsedLine;
			
			prep.className = preparsedLine.substring(0, preparsedLine.indexOf("("));
			
			prep.constraints = new ArrayList<>();
			String constraints = preparsedLine.substring(preparsedLine.indexOf("(")+1);
			constraints = constraints.substring(0, constraints.indexOf(")"));
			String[] constr = constraints.split(",");
			for(String cons : constr){
				Constraint constraint = new Constraint();
				int i = 0;
				if(!cons.isEmpty()){
					String[] pieces = cons.split("[ \\r\\n]");
					for(String piece : pieces){
						if(!piece.isEmpty()){
							if(i==0) constraint.variableName = piece;
							if(i==1) constraint.operator = piece;
							if(i==2) constraint.value = piece;
							i++;
						}
					}
					prep.constraints.add(constraint);
				}
				
			}
			retval.add(prep);
		}
		return retval;
	}

}
