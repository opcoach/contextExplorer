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

import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContextTableContentProvider implements IStructuredContentProvider
{

	EclipseContext selectedContext;

	@Override
	public void dispose()
	{
		selectedContext = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		selectedContext = (newInput instanceof EclipseContext) ? (EclipseContext) newInput : null;
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (selectedContext == null)
			return new Object[0];
		return selectedContext.localData().entrySet().toArray();
	}

}
