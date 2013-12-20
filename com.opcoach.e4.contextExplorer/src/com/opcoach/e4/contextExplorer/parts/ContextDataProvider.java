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
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.internal.contexts.Computation;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.opcoach.e4.contextExplorer.search.ContextRegistry;

/**
 * The column Label Provider used to display information in context data
 * treeviewer
 */
public class ContextDataProvider extends ColumnLabelProvider implements ITreeContentProvider
{

	private static final String NO_VALUES_FOUND = "No values found";
	private static final String INJECTED_IN_FIELD = "Injected in field :";
	private static final String INJECTED_IN_METHOD = "Injected in method :";
	private static final String INJECT_IMG_KEY = "inject";
	private static final String CONTEXT_FUNCTION_IMG_KEY = "contextFunction";
	private static final Color COLOR_IF_FOUND = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	private static final Object[] EMPTY_RESULT = new Object[0];
	public static final String LOCAL_VALUE_NODE = "Local values managed  by this context";
	public static final String INHERITED_INJECTED_VALUE_NODE = "Other values injected using this context";

	private ImageRegistry imgReg;

	@Inject
	private ContextRegistry contextRegistry;

	/** Store the selected context (get the current selection) */
	@SuppressWarnings("restriction")
	private static EclipseContext selectedContext;

	private Font boldFont;

	private boolean displayKey = false;

	@Inject
	public ContextDataProvider()
	{
		super();
		initFonts();
		initializeImageRegistry();
	}

	@Override
	public void dispose()
	{
		selectedContext = null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		selectedContext = (newInput instanceof EclipseContext) ? (EclipseContext) newInput : null;
	}

	public Object[] getElements(Object inputElement)
	{
		return new String[] { LOCAL_VALUE_NODE, INHERITED_INJECTED_VALUE_NODE };
	}

	public Object[] getChildren(Object inputElement)
	{
		if (selectedContext == null)
			return EMPTY_RESULT;

		if (inputElement == LOCAL_VALUE_NODE)
		{
			Collection<Object> result = new ArrayList<Object>();

			result.addAll(selectedContext.localData().entrySet());
			result.addAll(selectedContext.localContextFunction().entrySet());
			return result.toArray();
		} else if (inputElement == INHERITED_INJECTED_VALUE_NODE)
		{
			// Search for all values injected using this context but defined in
			// parent
			Collection<Object> result = new ArrayList<Object>();

			// Keep only the names that are not already displayed in local
			// values
			Collection<String> localKeys = selectedContext.localData().keySet();
			Collection<String> localContextFunctionsKeys = selectedContext.localContextFunction().keySet();

			if (selectedContext.getRawListenerNames() != null)
			{
				for (String name : selectedContext.getRawListenerNames())
				{
					if (!localKeys.contains(name) && !localContextFunctionsKeys.contains(name))
						result.add(name);
				}
			}
			return result.size() == 0 ? new String[] { NO_VALUES_FOUND } : result.toArray();

		} else if (inputElement instanceof Map.Entry)
		{
			Set<Computation> listeners = getListeners(inputElement);
			return listeners.toArray();
		} else if (inputElement instanceof String)
		{
			// This is the name of a raw listener in the inherited injected
			// value part
			return selectedContext.getListeners((String) inputElement).toArray();
		}

		return EMPTY_RESULT;
	}

	// private Font italicFont;

	public Font getBoldFont()
	{
		return boldFont;
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
		} else if (element instanceof Computation)
		{
			// For a computation : display field or method in key column and the
			// value in value
			String txt = super.getText(element);
			if (displayKey)
				return txt.contains("#") ? INJECTED_IN_METHOD : INJECTED_IN_FIELD;
			else
				return txt;
		}

		return displayKey ? super.getText(element) : null;
	}

	@Override
	public Color getForeground(Object element)
	{
		// Return blue color if the string matches the search
		String s = getText(element);
		return (contextRegistry.matchText(s)) ? COLOR_IF_FOUND : null;
	}

	/** Get the bold font for keys that are computed with ContextFunction */
	public Font getFont(Object element)
	{
		return isAContextKeyFunction(element) ? boldFont : null;
	}

	@Override
	public Image getImage(Object element)
	{
		if ((element == LOCAL_VALUE_NODE) || (element == INHERITED_INJECTED_VALUE_NODE))
		{
			return null;
		} else if (element instanceof Computation)
		{
			// For a computation : display field or method in key column and value in value column
			String txt = super.getText(element);
			return txt.contains("#") ? imgReg.get("publicMethod") : imgReg.get("publicField");

		}
		return displayKey && isAContextKeyFunction(element) ? imgReg.get(CONTEXT_FUNCTION_IMG_KEY) : null;
	}
	
	@Override
	public String getToolTipText(Object element)
	{
		if (isAContextKeyFunction(element))
			return "This element is computed by a ContextKey function";
		return super.getToolTipText(element);
	}

	@Override
	public Image getToolTipImage(Object object)
	{
		return getImage(object);
	}
	
	
	
	@Override
	public int getToolTipStyle(Object object)
	{
		return SWT.SHADOW_OUT;
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

	@Override
	public Object getParent(Object element)
	{
		// TODO Auto- method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if ((element == INHERITED_INJECTED_VALUE_NODE) || (element == LOCAL_VALUE_NODE))
		{
			return true; // Intermediate nodes returns true
		}

		Set<Computation> listeners = getListeners(element);
		return (listeners != null) && (listeners.size() > 0);
	}

	private Set<Computation> getListeners(Object element)
	{

		if (selectedContext != null)
		{
			if (element instanceof Map.Entry)
			{
				// Ask the context to know if there are listeners for this value
				Map.Entry<String, Object> mapEntry = (Map.Entry<String, Object>) element;
				String key = mapEntry.getKey();
				return selectedContext.getListeners(key);

			} else if (element instanceof String)
			{
				// Ask the context to know if there are listeners for this raw listener name
				return selectedContext.getListeners((String) element);
			}
		}
		return null;

	}
	@Inject
	@Optional
	public void listenToContext(@Named(IServiceConstants.ACTIVE_SELECTION) EclipseContext ctx)
	{
		selectedContext = ctx;
	}

	private void initializeImageRegistry()
	{
		Bundle b = org.eclipse.core.runtime.Platform.getBundle("com.opcoach.e4.contextExplorer");
		imgReg = new ImageRegistry();
		imgReg.put(CONTEXT_FUNCTION_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry("icons/contextFunction.png")));
		imgReg.put(INJECT_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry("icons/inject.png")));
		imgReg.put("publicMethod", ImageDescriptor.createFromURL(b.getEntry("icons/methpub_obj.gif")));
		imgReg.put("publicField", ImageDescriptor.createFromURL(b.getEntry("icons/field_public_obj.gif")));
	}

	private void initFonts()
	{
		FontData[] fontData = Display.getCurrent().getSystemFont().getFontData();
		String fontName = fontData[0].getName();
		FontRegistry registry = JFaceResources.getFontRegistry();
		boldFont = registry.getBold(fontName);
		// italicFont = registry.getItalic(fontName);
	}

}
