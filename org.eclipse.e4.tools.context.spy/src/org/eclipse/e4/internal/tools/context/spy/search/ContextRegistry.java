/*******************************************************************************
 * Copyright (c) 2013 OPCoach.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     OPCoach - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.internal.tools.context.spy.search;

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
 * a list of strings present in this context *
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

	private boolean ignoreWildCards;


	public void setPattern(String newPattern)
	{
		pattern = newPattern;
	}

	public void setIgnoreCase(boolean newIgnoreCase)
	{
		ignoreCase = newIgnoreCase;
	}

	public void setIgnoreWildCards(boolean ignoreWildCards)
	{
		this.ignoreWildCards = ignoreWildCards;
	}
	


	/**
	 * This method search for an object and check if it contains the text or a
	 * pattern matching this text
	 */
	public boolean containsText(IEclipseContext ctx)
	{
		if (pattern == null)
		{
			pattern = "";
		}
		matcher = new StringMatcher(pattern, ignoreCase, ignoreWildCards);

		// It is useless to store the values in a map, because context changes
		// everytime and it should be tracked.
		Collection<String> values = computeValues(ctx);

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
	@SuppressWarnings("restriction")
	private Collection<String> computeValues(IEclipseContext ctx)
	{
		Collection<String> result = new ArrayList<String>();
		if (ctx instanceof EclipseContext)
		{
			// Search for all strings in this context (values and context function)
			
			EclipseContext currentContext = (EclipseContext) ctx;
			extractStringsFromMap(currentContext.localData(), result);

			// Search also in context functions
				extractStringsFromMap(currentContext.localContextFunction(), result);
			
			
			// Search for the inherited values injected using this context but defined in
			// parent
			// Keep only the names that are not already displayed in local
			// values
			Collection<String> localKeys = currentContext.localData().keySet();
			Collection<String> localContextFunctionsKeys = currentContext.localContextFunction().keySet();

			if (currentContext.getRawListenerNames() != null)
			{
				for (String name : currentContext.getRawListenerNames())
				{
					if (!localKeys.contains(name) && !localContextFunctionsKeys.contains(name))
						result.add(name);
				}
			}

		} else
		{
			log.warn("Warning : the received EclipseContext has not the expected type. It is a : " + ctx.getClass().toString());
		}
		
		return result;
	}

	/**
	 * 
	 * @param map the map to extract the strings (keys and values)
	 * @param result the result to fill with strings
	 */
	private void extractStringsFromMap(Map<String, Object> map, Collection<String> result)
	{
		for (Map.Entry<String, Object> entry : map.entrySet())
		{
			result.add(entry.getKey().toString());
			Object value = entry.getValue();
			if (value != null)
			{
				result.add(value.toString());
			}
		}
	}

}
