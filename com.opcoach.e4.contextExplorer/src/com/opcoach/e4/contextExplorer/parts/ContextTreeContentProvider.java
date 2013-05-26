package com.opcoach.e4.contextExplorer.parts;

import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContextTreeContentProvider implements ITreeContentProvider
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
		if (inputElement instanceof MApplication)
		{
			return new Object [] {((MApplication) inputElement).getContext().getParent()};
		}
		
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof EclipseContext)
		{
			EclipseContext ct = (EclipseContext) parentElement;
			return ct.getChildren().toArray();
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
	}

}
