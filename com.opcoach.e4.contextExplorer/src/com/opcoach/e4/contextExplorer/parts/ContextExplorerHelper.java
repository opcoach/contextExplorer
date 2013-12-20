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
package com.opcoach.e4.contextExplorer.parts;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.internal.contexts.Computation;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.core.internal.contexts.WeakGroupedListenerList;
import org.osgi.framework.BundleContext;

/**
 * An helper class to get information inside context management system. This
 * class uses internal fields or methods defined in EclipseContext
 * 
 * @author olivier
 * 
 */
public class ContextExplorerHelper
{

	/**
	 * Get all the contexts created by EclipseContextFactory. It get values from
	 * field introspection. Should be rewritten if internal structure changes
	 * 
	 * @return a collection of contexts created by EclipseContextFactory
	 */
	static Collection<IEclipseContext> getAllBundleContexts()
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
	
	
	static void displayContextListener(EclipseContext ctx, String indent)
	{
		
		
		
		for (String c : ctx.getRawListenerNames())
		{
			System.out.println(indent+"Raw Listener : " + c);
			// System.out.println(indent+"Computation ("+c.getClass().getSimpleName() + ") : " + c.toString() + " valid = " + c.isValid());
		}		
		
		
	/*	Collection<IEclipseContext> result = Collections.emptyList();
		if (indent == null)
			indent = "";
		try
		{
			// Must use introspection to get the weak hash map (no getter).
			Field f = EclipseContext.class.getDeclaredField("weakListeners");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")
			WeakGroupedListenerList listeners = (WeakGroupedListenerList) f.get(ctx);

			System.out.println(indent+" -----     Names ------   ");
			for (String n : listeners.getNames())
				System.out.println(indent+"group name in weaklisteners : " + n);
			System.out.println(indent+" -----     Computations ------   ");
			for (Computation c : listeners.getListeners())
			{
				System.out.println(indent+"Computation ("+c.getClass().getSimpleName() + ") : " + c.toString() + " valid = " + c.isValid());
			}
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

		
		*/
		
		
		
	}

	
	
	
	

	
	
}
