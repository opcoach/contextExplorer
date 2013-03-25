package com.opcoach.e4.contextExplorer.parts;

import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContextTableContentProvider implements IStructuredContentProvider
{

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
