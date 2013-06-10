
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

public class ContextExplorerPart
{

	private TreeViewer tv;

	private TableViewer contentTable;

	@Inject
	private ESelectionService selService;

	public ContextExplorerPart()
	{
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent, MApplication a)
	{

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);

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

		createContextContentTable(a, sashForm);
		
		// Set the correct weight for sahsform
		sashForm.setWeights(new int[] { 15, 85 });


	}

	private void createContextContentTable(MApplication a, SashForm sashForm)
	{
		contentTable = new TableViewer(sashForm);
		contentTable.setContentProvider(new ContextTableContentProvider());

		// Create the table with 2 columns: key and value
		final Table cTable = contentTable.getTable();
		cTable.setHeaderVisible(true);
		cTable.setLinesVisible(true);
		GridData gd_cTable = new GridData(SWT.FILL);
		gd_cTable.verticalAlignment = SWT.TOP;
		cTable.setLayoutData(gd_cTable);

		// Create the first column for firstname
		TableViewerColumn firstNameCol = new TableViewerColumn(contentTable, SWT.NONE);
		firstNameCol.getColumn().setWidth(400);
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
				return (s.startsWith("com.opcoach")) ? Display.getCurrent().getSystemColor(SWT.COLOR_BLUE) : null;
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
	}
}
