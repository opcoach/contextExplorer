package com.opcoach.e4.contextExplorer.parts;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

@Creatable
@Singleton
public class ContextRegistry 
{

	private String pattern;

	private Map<Object, String> indexes;
	
	@Inject
	public ContextRegistry()
	{
		System.out.println("Creation du context Registry -> " + this);
	}
	
	public void setIndexes(Map<Object, String> indexes)
	{
		this.indexes = indexes;
	}
	
	
	public void setPattern(String newPattern)
	{
		pattern = newPattern;
	}
	

	/** This method search for an object and check if it contains the text or a pattern matching this text */
	public boolean containsText(Object o)
	{
		if (indexes == null)
			 return true;
		
		String val = indexes.get(o);
		return (val != null) ? val.startsWith(pattern) : false;
		
		
	}
}
