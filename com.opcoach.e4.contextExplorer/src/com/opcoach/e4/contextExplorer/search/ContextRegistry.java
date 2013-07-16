package com.opcoach.e4.contextExplorer.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
 * @author olivier
 * 
 */
@Creatable
@Singleton
public class ContextRegistry
{

	@Inject Logger log;
	
	private StringMatcher matcher; 

	/** The map of all the strings available in all contexts */
	private Map<IEclipseContext, Collection<String>> indexes;

	@Inject
	public ContextRegistry()
	{
		System.out.println("Creation du context Registry -> " + this);
		indexes = new HashMap<IEclipseContext, Collection<String>>();
	}

	public void setIndexes(Map<IEclipseContext, Collection<String>> indexes)
	{
		this.indexes = indexes;
	}

	public void setPattern(String newPattern)
	{
		matcher = new StringMatcher(newPattern, false, false); // ignore case but not wildcards
	}

	/**
	 * This method search for an object and check if it contains the text or a
	 * pattern matching this text
	 */
	public boolean containsText(IEclipseContext ctx)
	{
		if (indexes == null)
			return true;

		Collection<String> values = indexes.get(ctx);
		log.warn("Voir ici si le nombre de string attendues a chang�, car on ne peut pas �couter le contexte");
		if (values == null) 
		{
			values = computeValues(ctx);
			indexes.put(ctx, values);
		}

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
	
	/** Extract all string values in context
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
				result.add(entry.getValue().toString());
			}
		}
		else
		{
			log.warn("Warning : the received EclipseContext has not the expected type. It is a : " + ctx.getClass().toString());
		}
		return result;
	}

}
