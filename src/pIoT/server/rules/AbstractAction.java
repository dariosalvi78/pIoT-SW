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

/**
 * This is an abstract Action.
 * Its implementation simply provides a mechanism for naming the object, 
 * and bases the {@link #equals(Object)} and {@link #hashCode()}
 * on that name.
 * 
 * @see IAction
 */
public abstract class AbstractAction  {

	private final String name;

	public AbstractAction(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AbstractAction other = (AbstractAction) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Action [name=" + name + "]";
	}
	
	/**
	 * The function to be implemented by the action.
	 * @param parameters parameters, in form of String
	 */
	public abstract void execute(String parameters);
	
}
