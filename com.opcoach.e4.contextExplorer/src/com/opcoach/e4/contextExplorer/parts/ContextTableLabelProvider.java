package com.opcoach.e4.contextExplorer.parts;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import com.opcoach.e4.contextExplorer.search.ContextRegistry;

/** The column Label Provider used to display information in context Explorer */
public class ContextTableLabelProvider extends ColumnLabelProvider implements IFontProvider
{

	@Inject
	private ContextRegistry contextRegistry;

	/** Store the selected context (get the current selection) */
	private EclipseContext selectedContext;

	private Font boldFont;

	// private Font italicFont;

	public Font getBoldFont()
	{
		return boldFont;
	}

	@Inject
	@Optional
	private void storeCurrentContext(@Named(IServiceConstants.ACTIVE_SELECTION) EclipseContext ctx)
	{
		selectedContext = ctx;
	}

	private boolean displayKey = false;

	@Inject
	public ContextTableLabelProvider()
	{
		super();
		computeFonts();
	}

	public void setDisplayKey(boolean k)
	{
		displayKey = k;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getText(Object element)
	{
		if (element instanceof Map.Entry)
		{
			Map.Entry<String, Object> mapEntry = (Map.Entry<String, Object>) element;
			Object o = displayKey ? mapEntry.getKey() : mapEntry.getValue();
			return (o == null) ? "null" : o.toString();
		}
		return super.getText(element);
	}

	@Override
	public Color getForeground(Object element)
	{
		// Return blue color if the string matches the search
		String s = getText(element);
		return (contextRegistry.matchText(s)) ? Display.getCurrent().getSystemColor(SWT.COLOR_BLUE) : null;
	}

	/** Get the bold font for keys that are computed with ContextFunction */
	public Font getFont(Object element)
	{
		return isAContextKeyFunction(element) ? boldFont : null;
	}

	/**
	 * Compute it the current entry in context is a context function
	 * 
	 * @param element
	 * @return true if element is a context function
	 */
	@SuppressWarnings("restriction")
	boolean isAContextKeyFunction(Object element)
	{
		if (selectedContext != null && element instanceof Map.Entry)
		{
			// Just check if key in element is a key in the map of context
			// functions.
			Map.Entry<String, Object> mapEntry = (Map.Entry<String, Object>) element;
			return (selectedContext.localContextFunction().containsKey(mapEntry.getKey()));
		}
		return false;

	}

	protected void computeFonts()
	{
		FontData[] fontData = Display.getCurrent().getSystemFont().getFontData();
		String fontName = fontData[0].getName();
		FontRegistry registry = JFaceResources.getFontRegistry();
		boldFont = registry.getBold(fontName);
		// italicFont = registry.getItalic(fontName);
	}

}
