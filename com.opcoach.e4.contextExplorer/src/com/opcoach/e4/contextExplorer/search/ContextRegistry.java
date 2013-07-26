package com.opcoach.e4.contextExplorer.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.core.services.log.Logger;

/**
 * Register for each context in the application, all strings for keys and values
 * so as to filter the tree the main map contains : an IEclipseContext as a key
 * a list of strings present in this context
 * 
 * It extends RunAndTrack to be informed of contexts updates
 * 
 * @author olivier
 * 
 */
@Creatable
@Singleton
public class ContextRegistry 
{

	@Inject
	Logger log;

	private StringMatcher matcher;

	private String pattern;
	
	private boolean ignoreCase;
	
	@Inject
	public ContextRegistry()
	{
	}

	public void setPattern(String newPattern)
	{
		pattern = newPattern;
		//matcher = new StringMatcher(newPattern, false, false); // do not ignore case and wildcards
	}
	
	public void setIgnoreCase(boolean newIgnoreCase) {
		ignoreCase = newIgnoreCase;
	}

	/**
	 * This method search for an object and check if it contains the text or a
	 * pattern matching this text
	 */
	public boolean containsText(IEclipseContext ctx)
	{
		if (pattern == null) {
			pattern = "";
		}
		matcher = new StringMatcher(pattern, ignoreCase, false);
		
		// It is useless to store the values in a map, because context changes everytime and it should be tracked. 
		Collection<String> values =  computeValues(ctx);
			
		// Search for a string matching the pattern
		boolean found = false;
		for (String s : values)
		{
			if (matchText(s))
			{
				found = true;
				break;
			}
		}
		return found;
	}

	public boolean matchText(String text)
	{
		return (matcher != null) && matcher.match(text);
	}

	/**
	 * Extract all string values in context
	 * 
	 * @param ctx
	 * @return
	 */
	private Collection<String> computeValues(IEclipseContext ctx)
	{
		Collection<String> result = new ArrayList<String>();
		if (ctx instanceof EclipseContext)
		{
			// Search for all strings in this context...
			for (Map.Entry<String, Object> entry : ((EclipseContext) ctx).localData().entrySet())
			{
				result.add(entry.getKey().toString());
				Object value = entry.getValue();
				if (value != null) {
					result.add(value.toString());
				}
			}
		} else
		{
			log.warn("Warning : the received EclipseContext has not the expected type. It is a : " + ctx.getClass().toString());
		}
		return result;
	}

}
