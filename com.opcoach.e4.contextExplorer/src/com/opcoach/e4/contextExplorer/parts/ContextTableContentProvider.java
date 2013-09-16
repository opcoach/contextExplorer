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

import javax.inject.Inject;

import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContextTableContentProvider implements IStructuredContentProvider
{
		
	@Inject
   public ContextTableContentProvider()
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
		if (inputElement instanceof EclipseContext)
			return ((EclipseContext) inputElement).localData().entrySet().toArray();
		return null;
	}


}
