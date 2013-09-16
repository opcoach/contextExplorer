package com.opcoach.e4.contextExplorer.parts;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.opcoach.e4.contextExplorer.search.ContextRegistry;

/** The column Label Provider used to display information in context Explorer */
public class ContextTableLabelProvider extends ColumnLabelProvider
{

	@Inject
	private ContextRegistry contextRegistry;
	
	// Get the current selected context
	@Optional @Named(IServiceConstants.ACTIVE_SELECTION) EclipseContext ctx;
	

	private boolean displayKey = false;

	@Inject
	public ContextTableLabelProvider()
	{
		super();
		displayKey = false;
	}
	
	public void setDisplayKey(boolean k)
	{
		displayKey = k;
	}
	

	@Override
	public String getText(Object element)
	{ 
		@SuppressWarnings("unchecked")
		Map.Entry<String, Object> mapEntry = (Map.Entry<String, Object>) element;
		Object o = displayKey ? mapEntry.getKey() : mapEntry.getValue();
		return (o == null) ? "null" : o.toString();
	}

	@Override
	public Color getForeground(Object element)
	{
		String s = getText(element); // TODO : to be optimised (already computed)
		return (contextRegistry.matchText(s)) ? Display.getCurrent().getSystemColor(SWT.COLOR_BLUE) : null;
	}
	


	
	
}
