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

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContextTreeProvider extends LabelProvider implements ITreeContentProvider
{

	@Inject
	private ContextRegistry contextRegistry;
	
	@Inject
	public ContextTreeProvider()
	{
		
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof MApplication)
		{
			return new Object[] { ((MApplication) inputElement).getContext().getParent() };
		}

		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof EclipseContext)
		{
			Collection<IEclipseContext> result = new ArrayList<IEclipseContext>();
			EclipseContext ct = (EclipseContext) parentElement;
			for (IEclipseContext child : ct.getChildren())
			{
				if (contextRegistry.containsText(getText(child)))
				{
					// Keep it anyway...
					result.add(child);
				} else
				{
					// Must check if one of the table elements could contain the
					// string
					for (Object o : ((EclipseContext) child).localData().values())
					{
						if ((o != null) && contextRegistry.containsText(o.toString()))
						{
							result.add(child);
							break;
						}
					}

				}
			}
			return result.toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		return true;
		
	/*	if (element instanceof EclipseContext)
		{
			EclipseContext ct = (EclipseContext) element;
			return (ct.getChildren().size() > 0);
		}
		return false; */

	}

	@Override
	public String getText(Object element)
	{
		return super.getText(element);
	}

}
