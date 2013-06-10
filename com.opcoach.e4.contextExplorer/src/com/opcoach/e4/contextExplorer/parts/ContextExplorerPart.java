
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

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class ContextExplorerPart
{

	private TreeViewer tv;

	private TableViewer contentTable;

	@Inject
	private ESelectionService selService;
	
	
	/** Store the prefix used to highlight objects in the table */
	private String prefixForColor = "com.";
	

	public ContextExplorerPart()
	{
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent, MApplication a)
	{
		parent.setLayout(new GridLayout(1,false));
		
		// Create a sahsform with the tree in the top part
		// And a composite in the bottom part, containing another composite for the color filter and a table viewer : 
		// parent
		//    SashForm
		//      TreeViewer
		//      Composite
		//         Composite for color filter
		//         TableViewer
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// TreeViewer on the top
		tv = new TreeViewer(sashForm);
		tv.setContentProvider(new ContextTreeContentProvider());
		tv.setLabelProvider(new ContextLabelProvider());
		tv.setInput(a);
		tv.expandAll();

		tv.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				selService.setSelection((ss.size() == 1) ? ss.getFirstElement() : ss.toArray());

			}
		});
		
		// The composite for the bottom of sahsform
		Composite bottom =  new Composite(sashForm, SWT.NONE);
		bottom.setLayout(new GridLayout(1,false));
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// The composite to manage the color filter
		final Composite comp = new Composite(bottom, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		Label title = new Label(comp, SWT.NONE);
		title.setText("Prefix used for color :");
		final Text colorFilter = new Text(comp, SWT.BORDER);
		colorFilter.setText(prefixForColor);
		GridDataFactory.fillDefaults().hint(180, SWT.DEFAULT).applyTo(colorFilter);
		colorFilter.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				// Update the text for prefixForColor and refresh the table
				prefixForColor = colorFilter.getText();
				contentTable.refresh(true);
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) { // Nothing to do 
				}
			});
		
		// Create the table in bottom
		createContextContentTable(a, bottom);
	
		// Set the correct weight for sahsform
		sashForm.setWeights(new int[] { 20, 80 });


	}

	

	private void createContextContentTable(MApplication a, Composite parent)
	{
		contentTable = new TableViewer(parent);
		contentTable.setContentProvider(new ContextTableContentProvider());

		// Create the table with 2 columns: key and value
		final Table cTable = contentTable.getTable();
		cTable.setHeaderVisible(true);
		cTable.setLinesVisible(true);
		GridData gd_cTable = new GridData(SWT.FILL, SWT.FILL, true, true);
		//gd_cTable.verticalAlignment = SWT.TOP;
		cTable.setLayoutData(gd_cTable);

		// Create the first column for firstname
		TableViewerColumn firstNameCol = new TableViewerColumn(contentTable, SWT.NONE);
		firstNameCol.getColumn().setWidth(250);
		firstNameCol.getColumn().setText("Key");
		firstNameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element)
			{
				return ((Map.Entry<String, Object>) element).getKey().toString();
			}

			@Override
			public Color getForeground(Object element)
			{
				String s = ((Map.Entry<String, Object>) element).getKey();
				boolean color = (prefixForColor.length() > 0) && s.startsWith(prefixForColor);
				return color ? Display.getCurrent().getSystemColor(SWT.COLOR_BLUE) : null;
			}
		});

		// Create the second column for name
		TableViewerColumn nameCol = new TableViewerColumn(contentTable, SWT.NONE);
		nameCol.getColumn().setWidth(600);
		nameCol.getColumn().setText("Value");
		nameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element)
			{
				Object val = ((Map.Entry<String, Object>) element).getValue();
				return (val == null) ? "null" : val.toString();
			}
		});

		// Set input data and content provider (default ArrayContentProvider)
		contentTable.setSorter(new ViewerSorter());
		contentTable.setInput(a.getContext().getParent());
		
	}

	@Inject
	public void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) EclipseContext ctx)
	{
		if (ctx == null)
			return;
		contentTable.setInput(ctx);
		contentTable.refresh();
	}

	@PreDestroy
	public void dispose()
	{
	}

	@Focus
	public void setFocus()
	{
		tv.getTree().setFocus();
	}
}
