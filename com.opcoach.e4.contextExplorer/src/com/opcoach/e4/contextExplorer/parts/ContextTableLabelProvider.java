package com.opcoach.e4.contextExplorer.parts;

import java.util.Map;

import javax.inject.Inject;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/** The column Label Provider used to display information in context Explorer */
public class ContextTableLabelProvider extends ColumnLabelProvider
{

	@Inject
	private ContextRegistry contextRegistry;
	

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
		
		Map.Entry<String, Object> mapEntry = (Map.Entry<String, Object>) element;
		Object o = displayKey ? mapEntry.getKey() : mapEntry.getValue();
		return (o == null) ? "null" : o.toString();
	}

	@Override
	public Color getForeground(Object element)
	{
		String s = getText(element);
		boolean color = contextRegistry.containsText(element);
		return color ? Display.getCurrent().getSystemColor(SWT.COLOR_BLUE) : null;
	}
	


	
	
}
