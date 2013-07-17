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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import com.opcoach.e4.contextExplorer.search.ContextRegistry;

public class ContextExplorerPart
{

	private TreeViewer tv;

	private TableViewer contentTable;

	@Inject
	private ESelectionService selService;

	private ContextTreeProvider treeContentProvider;

	private ContextTableContentProvider tableContentProvider;

	private ContextTableLabelProvider keyLabelProvider;

	private ContextTableLabelProvider valueLabelProvider;

	/** Store the prefix used to highlight objects in the table */
	private String prefixForColor = "com.";

	private ImageRegistry imgReg;

	@Inject
	private ContextRegistry contextRegistry;

	@Inject
	private void initializeImageRegistry()
	{
		Bundle b = org.eclipse.core.runtime.Platform.getBundle("com.opcoach.e4.contextExplorer");
		imgReg = new ImageRegistry();
		imgReg.put("collapseall", ImageDescriptor.createFromURL(b.getEntry("icons/collapseall.gif")));
		imgReg.put("expandall", ImageDescriptor.createFromURL(b.getEntry("icons/expandall.gif")));
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent, MApplication a, IEclipseContext ctx)
	{
		parent.setLayout(new GridLayout(1, false));

		// borderColor = new Color(parent.getDisplay(), 170, 176, 191);
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(5, false));

		Button expandAll = new Button(comp, SWT.FLAT);
		expandAll.setImage(imgReg.get("expandall"));
		expandAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tv.expandAll();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		Button collapseAll = new Button(comp, SWT.FLAT);
		collapseAll.setImage(imgReg.get("collapseall"));
		collapseAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tv.collapseAll();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		/*
		 * Label title = new Label(comp, SWT.NONE);
		 * title.setText("Prefix used for color :"); final Text colorFilter =
		 * new Text(comp, SWT.BORDER); colorFilter.setText(prefixForColor);
		 * colorFilter
		 * .setToolTipText("Enter here the prefix to be highlighted in table");
		 * GridDataFactory.fillDefaults().hint(180,
		 * SWT.DEFAULT).applyTo(colorFilter); colorFilter.addKeyListener(new
		 * KeyListener() {
		 * 
		 * @Override public void keyReleased(KeyEvent e) { // Update the text
		 * for prefixForColor and refresh the table prefixForColor =
		 * colorFilter.getText(); contentTable.refresh(true);
		 * 
		 * }
		 * 
		 * @Override public void keyPressed(KeyEvent e) { // Nothing to do } });
		 */

		// Do the search widget
		final Text text = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH);
		GridDataFactory.fillDefaults().hint(250, SWT.DEFAULT).applyTo(text);
		text.setMessage("Search data");
		text.setToolTipText("Highlight the contexts where contained objects match this string pattern");
		text.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e)
			{
				contextRegistry.setPattern(text.getText());
				tv.refresh(true);
				contentTable.refresh(true);
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				// TODO Auto-generated method stub

			}
		});

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// TreeViewer on the top
		tv = new TreeViewer(sashForm);
		treeContentProvider = ContextInjectionFactory.make(ContextTreeProvider.class, ctx);
		tv.setContentProvider(treeContentProvider);
		tv.setLabelProvider(treeContentProvider);
		tv.setInput(a);

		tv.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				selService.setSelection((ss.size() == 1) ? ss.getFirstElement() : ss.toArray());

			}
		});

		createContextContentTable(a, sashForm, ctx);

		// Set the correct weight for sahsform
		sashForm.setWeights(new int[] { 15, 85 });

		// Open all the tree
		tv.expandAll();

	}

	private void createContextContentTable(MApplication a, SashForm sashForm, IEclipseContext ctx)
	{
		contentTable = new TableViewer(sashForm);
		tableContentProvider = ContextInjectionFactory.make(ContextTableContentProvider.class, ctx);
		contentTable.setContentProvider(tableContentProvider);

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
		keyLabelProvider = ContextInjectionFactory.make(ContextTableLabelProvider.class, ctx);
		keyLabelProvider.setDisplayKey(true);
		firstNameCol.setLabelProvider(keyLabelProvider);

		// Create the second column for name
		TableViewerColumn nameCol = new TableViewerColumn(contentTable, SWT.NONE);
		nameCol.getColumn().setWidth(600);
		nameCol.getColumn().setText("Value");
		valueLabelProvider = ContextInjectionFactory.make(ContextTableLabelProvider.class, ctx);
		nameCol.setLabelProvider(valueLabelProvider);

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
		tv.getControl().setFocus();
	}

	
}
