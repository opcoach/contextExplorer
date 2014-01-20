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
package org.eclipse.e4.internal.tools.context.spy;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleContext;

/**
 * A helper class to get information inside context management system. This
 * class uses internal fields or methods defined in EclipseContext
 * Could be updated in the future.
 * 
 * @author olivier
 * 
 */
public class ContextSpyHelper
{

	/**
	 * Get all the contexts created by EclipseContextFactory. It get values from
	 * field introspection. Should be rewritten if internal structure changes
	 * 
	 * @return a collection of contexts created by EclipseContextFactory
	 */
	public static Collection<IEclipseContext> getAllBundleContexts()
	{
		Collection<IEclipseContext> result = Collections.emptyList();
		try
		{
			// Must use introspection to get the weak hash map (no getter).
			Field f = EclipseContextFactory.class.getDeclaredField("serviceContexts");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<BundleContext, IEclipseContext> ctxs = (Map<BundleContext, IEclipseContext>) f.get(null);
			result = ctxs.values();

		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return result;

	}
	

	
	
}
