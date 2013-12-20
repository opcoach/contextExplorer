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
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.internal.contexts.Computation;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import com.opcoach.e4.contextExplorer.search.ContextRegistry;

public class ContextExplorerPart
{

	private TreeViewer tv;

	// private TableViewer contentTable;

	@Inject
	private ESelectionService selService;

	private ContextTreeProvider treeContentProvider;

	private ContextTableContentProvider tableContentProvider;

	private ContextTableLabelProvider keyLabelProvider;

	private ContextTableLabelProvider valueLabelProvider;

	private ImageRegistry imgReg;

	private ContextEntryComparator comparator;

	@Inject
	private ContextRegistry contextRegistry;

	private ContextDataPart contextDataPart;

	
	@Inject public void testInjections(ESelectionService myservice) { }
	
	@Inject
	private void initializeImageRegistry()
	{
		Bundle b = org.eclipse.core.runtime.Platform.getBundle("com.opcoach.e4.contextExplorer");
		imgReg = new ImageRegistry();
		imgReg.put("collapseall", ImageDescriptor.createFromURL(b.getEntry("icons/collapseall.gif")));
		imgReg.put("expandall", ImageDescriptor.createFromURL(b.getEntry("icons/expandall.gif")));
		imgReg.put("refresh", ImageDescriptor.createFromURL(b.getEntry("icons/refresh.gif")));
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent, MApplication a, IEclipseContext ctx)
	{
		parent.setLayout(new GridLayout(1, false));

		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(6, false));

		Button refreshButton = new Button(comp, SWT.FLAT);
		refreshButton.setImage(imgReg.get("refresh"));
		refreshButton.addSelectionListener(new SelectionListener()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					tv.refresh(true);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		Button expandAll = new Button(comp, SWT.FLAT);
		expandAll.setImage(imgReg.get("expandall"));
		expandAll.addSelectionListener(new SelectionListener()
			{

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
		collapseAll.addSelectionListener(new SelectionListener()
			{

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

		// Do the search widget
		final Text text = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH);
		GridDataFactory.fillDefaults().hint(250, SWT.DEFAULT).applyTo(text);
		text.setMessage("Search data");
		text.setToolTipText("Highlight the contexts where contained objects match this string pattern");
		text.addKeyListener(new KeyListener()
			{

				@Override
				public void keyReleased(KeyEvent e)
				{
					contextRegistry.setPattern(text.getText());
					tv.refresh(true);
					contextDataPart.refresh(true);
				}

				@Override
				public void keyPressed(KeyEvent e)
				{
					// TODO Auto-generated method stub

				}
			});

		final Button ignoreCase = new Button(comp, SWT.CHECK);
		ignoreCase.setText("Ignore case");
		ignoreCase.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					contextRegistry.setIgnoreCase(ignoreCase.getSelection());
					tv.refresh(true);
					contextDataPart.refresh(true);
				}
			});

		final Button ignoreWildCards = new Button(comp, SWT.CHECK);
		ignoreWildCards.setText("Ignore WildCards");
		ignoreWildCards.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					contextRegistry.setIgnoreWildCards(ignoreWildCards.getSelection());
					tv.refresh(true);
					contextDataPart.refresh(true);
				}
			});

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL | SWT.V_SCROLL | SWT.H_SCROLL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		

		// TreeViewer on the top
		tv = new TreeViewer(sashForm);
		treeContentProvider = ContextInjectionFactory.make(ContextTreeProvider.class, ctx);
		tv.setContentProvider(treeContentProvider);
		tv.setLabelProvider(treeContentProvider);
		tv.setSorter(new ViewerSorter());

		// tv.setInput(a);
		tv.setInput(ContextExplorerHelper.getAllBundleContexts());

		tv.addSelectionChangedListener(new ISelectionChangedListener()
			{

				@Override
				public void selectionChanged(SelectionChangedEvent event)
				{
					IStructuredSelection ss = (IStructuredSelection) event.getSelection();
					selService.setSelection((ss.size() == 1) ? ss.getFirstElement() : ss.toArray());

				}
			});

		IEclipseContext subCtx = ctx.createChild("Context for ContextDataPart");
		subCtx.set(Composite.class, sashForm);
		contextDataPart = ContextInjectionFactory.make(ContextDataPart.class, subCtx);
		// Call directly the createControls to avoid a context creation with Composite to do this.
		//contextDataPart.createControls(sashForm, ctx);   
		// But inject it anyway :)
	//	ContextInjectionFactory.inject(contextDataPart, ctx);
		
		
		// createContextContentTable(a, sashForm, ctx);

		// Set the correct weight for sahsform
		sashForm.setWeights(new int[] { 35, 65 });

		// Open all the tree
		tv.expandAll();

	}

/*	private void createContextContentTable(MApplication a, SashForm sashForm, IEclipseContext ctx)
	{
		
		final Composite comp = new Composite(sashForm, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));


		final Button showContextFunctions = new Button(comp, SWT.CHECK);
		showContextFunctions.setText("Show context functions");
		showContextFunctions.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					tableContentProvider.showContextFunctions(showContextFunctions.getSelection());
					contextRegistry.setContextFunctionDisplayed(showContextFunctions.getSelection());
					contentTable.refresh(true);
				}
			});
		showContextFunctions.setToolTipText("Select this box to show in the table values computed by context functions");

		
		contentTable = new TableViewer(comp);
		tableContentProvider = ContextInjectionFactory.make(ContextTableContentProvider.class, ctx);
		contentTable.setContentProvider(tableContentProvider);
		
		// Create the table with 2 columns: key and value
		final Table cTable = contentTable.getTable();
		cTable.setHeaderVisible(true);
		cTable.setLinesVisible(true);
		cTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));


		// Create the first column for the key
		TableViewerColumn keyCol = new TableViewerColumn(contentTable, SWT.NONE);
		keyCol.getColumn().setWidth(400);
		keyCol.getColumn().setText("Key");
		keyLabelProvider = ContextInjectionFactory.make(ContextTableLabelProvider.class, ctx);
		keyLabelProvider.setDisplayKey(true);
		keyCol.setLabelProvider(keyLabelProvider);
		keyCol.getColumn().addSelectionListener(getHeaderSelectionAdapter(contentTable, keyCol.getColumn(), 0, keyLabelProvider));

		// Create the second column for the value
		TableViewerColumn valueCol = new TableViewerColumn(contentTable, SWT.NONE);
		valueCol.getColumn().setWidth(600);
		valueCol.getColumn().setText("Value");
		valueLabelProvider = ContextInjectionFactory.make(ContextTableLabelProvider.class, ctx);
		valueCol.setLabelProvider(valueLabelProvider);
		valueCol.getColumn().addSelectionListener(getHeaderSelectionAdapter(contentTable, valueCol.getColumn(), 1, valueLabelProvider));

		// Add the sort stuff to manage clic on header and the
		// customerComparator
		comparator = new ContextEntryComparator(0, keyLabelProvider);
		
		// Put the button in bold font
		showContextFunctions.setFont(keyLabelProvider.getBoldFont());
		
		// Set input data and content provider (default ArrayContentProvider)
		contentTable.setInput(a.getContext().getParent());
		
		contentTable.addSelectionChangedListener(new ISelectionChangedListener()
		{

			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
			
				EclipseContext currentContext = (EclipseContext) ((IStructuredSelection) tv.getSelection()).getFirstElement();

				Object selectedInContext = ((IStructuredSelection) event.getSelection()).getFirstElement();
				String key = "";

				if (selectedInContext instanceof Map.Entry)
				{
				Map.Entry<String,Object> value = (Map.Entry<String,Object>) selectedInContext;
				System.out.println("Element selectionné" + value);
				if (value == null)
					return;
				key=value.getKey();
			}
				else if (selectedInContext instanceof String)
				{
					key = (String) selectedInContext;
				}
			
				System.out.println("---------------   Listeners on " + key + "------------------");

				Set<Computation> listeners = (currentContext != null) ? currentContext.getListeners(key) : null;
				if (listeners != null)
				{
				for (Computation c : listeners )
					System.out.println("Computation ("+c.getClass().getSimpleName() + ") : " + c.toString() + " valid = " + c.isValid());
				}
				System.out.println("---------------   End listeners ------------------");
				

				
				System.out.println("Contexte selectionné : " + currentContext);
				System.out.println("...........................");
				String indent = "";
				do
				{
					System.out.println(indent + "Contexte Courant : " + currentContext);
					ContextExplorerHelper.displayContextListener(currentContext, indent);
					currentContext = currentContext.getParent();
					indent = indent + "   ";

				} while (currentContext != null);

			}
		});

	} */

/*	@Inject
	public void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) EclipseContext ctx)
	{
		if (ctx == null)
		{
			return;
		}
		contentTable.setInput(ctx);
		contentTable.setComparator(null);
		contentTable.refresh();
	} */

	@PreDestroy
	public void dispose()
	{
	}

	@Focus
	public void setFocus()
	{
		tv.getControl().setFocus();
	}

	/**
	 * An entry comparator for the table, dealing with column index, keys and values
	 */
	public class ContextEntryComparator extends ViewerComparator
	{
		private int columnIndex;
		private int direction;
		private ILabelProvider labelProvider;

		public ContextEntryComparator(int columnIndex, ILabelProvider defaultLabelProvider)
		{
			this.columnIndex = columnIndex;
			direction = SWT.DOWN;
			labelProvider = defaultLabelProvider;
		}

		public int getDirection()
		{
			return direction;
		}

		/** Called when click on table header */
		public void setColumn(int column)
		{
			if (column == columnIndex)
			{
				// Same column as last sort; toggle the direction
				direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP;
			} else
			{
				// New column; do a descending sort
				columnIndex = column;
				direction = SWT.DOWN;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			// Get the text from label provider to compare.
			String s1 = labelProvider.getText(e1).toLowerCase();
			String s2 = labelProvider.getText(e2).toLowerCase();
			int rc = s1.compareTo(s2);
			// If descending order, flip the direction
			return (direction == SWT.DOWN) ? -rc : rc;
		}

		public void setLabelProvider(ILabelProvider textProvider)
		{
			labelProvider = textProvider;
		}

	}

	private SelectionAdapter getHeaderSelectionAdapter(final TableViewer viewer, final TableColumn column, final int columnIndex,final ILabelProvider textProvider)
	{
		SelectionAdapter selectionAdapter = new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					viewer.setComparator(comparator);

					comparator.setColumn(columnIndex);
					comparator.setLabelProvider(textProvider);
					viewer.getTable().setSortDirection(comparator.getDirection());
					viewer.getTable().setSortColumn(column);
					viewer.refresh();
				}
			};
		return selectionAdapter;
	}

}
