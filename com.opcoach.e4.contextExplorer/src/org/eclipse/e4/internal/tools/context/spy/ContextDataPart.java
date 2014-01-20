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
package org.eclipse.e4.internal.tools.context.spy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * This part listen to selection, and if it is an EclipseContext,
 * it displays its information It is used in the integrated ContextSpyPart
 * and (in the future) it could be used outside to display the context of focused part for instance
 * */
public class ContextDataPart
{
	private TreeViewer contextDataViewer;

	private ContextDataProvider dataProvider;

	private ContextEntryComparator comparator;


	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent, IEclipseContext ctx)
	{

		parent.setLayout(new GridLayout(1, false));

		// TreeViewer on the top
		contextDataViewer = new TreeViewer(parent);
		dataProvider = ContextInjectionFactory.make(ContextDataProvider.class, ctx);
		contextDataViewer.setContentProvider(dataProvider);
		contextDataViewer.setLabelProvider(dataProvider);
		// contextContentTv.setSorter(new ViewerSorter());

		final Tree cTree = contextDataViewer.getTree();
		cTree.setHeaderVisible(true);
		cTree.setLinesVisible(true);
		cTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// tv.setInput(a);
		contextDataViewer.setInput("Foo"); // getElements starts alone

		// Add columns in the tree
		// Create the first column for the key
		TreeViewerColumn keyCol = new TreeViewerColumn(contextDataViewer, SWT.NONE);
		keyCol.getColumn().setWidth(400);
		keyCol.getColumn().setText("Key");
		ContextDataProvider keyLabelProvider = ContextInjectionFactory.make(ContextDataProvider.class, ctx);
		keyLabelProvider.setDisplayKey(true);
		keyCol.setLabelProvider(keyLabelProvider);
		keyCol.getColumn().setToolTipText("Key in context");
		keyCol.getColumn().addSelectionListener(
				getHeaderSelectionAdapter(contextDataViewer, keyCol.getColumn(), 0, keyLabelProvider));

		comparator = new ContextEntryComparator(0, keyLabelProvider);
		contextDataViewer.setComparator(comparator);

		// Create the second column for the value
		TreeViewerColumn valueCol = new TreeViewerColumn(contextDataViewer, SWT.NONE);
		valueCol.getColumn().setWidth(600);
		valueCol.getColumn().setText("Value");
		ContextDataProvider valueLabelProvider = ContextInjectionFactory.make(ContextDataProvider.class, ctx);
		valueCol.setLabelProvider(dataProvider);
		valueCol.getColumn().addSelectionListener(
				getHeaderSelectionAdapter(contextDataViewer, valueCol.getColumn(), 1, valueLabelProvider));

		// Open all the tree
		contextDataViewer.expandAll();

		ColumnViewerToolTipSupport.enableFor(contextDataViewer);

	}

	@PreDestroy
	public void dispose()
	{
	}

	@Focus
	public void setFocus()
	{
		contextDataViewer.getControl().setFocus();
	}

	@SuppressWarnings("restriction")
	@Inject
	@Optional
	public void listenToContext(@Named(IServiceConstants.ACTIVE_SELECTION) EclipseContext ctx)
	{
		if (ctx == null)
		{
			return;
		}
		contextDataViewer.setInput(ctx);
		contextDataViewer.expandToLevel(2);
	}

	/**
	 * An entry comparator for the table, dealing with column index, keys and
	 * values
	 */
	public class ContextEntryComparator extends ViewerComparator
	{
		private int columnIndex;
		private int direction;
		private ILabelProvider labelProvider;

		public ContextEntryComparator(int columnIndex, ILabelProvider defaultLabelProvider)
		{
			this.columnIndex = columnIndex;
			direction = SWT.UP;
			labelProvider = defaultLabelProvider;
		}

		public int getDirection()
		{
			return direction;
		}

		/** Called when click on table header, reverse order */
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
			// For root elements at first level, we keep Local before Inherited
			if ((e1 == ContextDataProvider.LOCAL_VALUE_NODE) || (e2 == ContextDataProvider.LOCAL_VALUE_NODE))
				return -1;

			// Now can compare the text from label provider.
			String lp1 = labelProvider.getText(e1);
			String lp2 = labelProvider.getText(e2);
			String s1 = lp1 == null ? "" : lp1.toLowerCase();
			String s2 = lp2 == null ? "" : lp2.toLowerCase();
			int rc = s1.compareTo(s2);
			// If descending order, flip the direction
			return (direction == SWT.DOWN) ? -rc : rc;
		}

		public void setLabelProvider(ILabelProvider textProvider)
		{
			labelProvider = textProvider;
		}

	}

	private SelectionAdapter getHeaderSelectionAdapter(final TreeViewer viewer, final TreeColumn column, final int columnIndex,
			final ILabelProvider textProvider)
	{
		SelectionAdapter selectionAdapter = new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					viewer.setComparator(comparator);
					comparator.setColumn(columnIndex);
					comparator.setLabelProvider(textProvider);
					viewer.getTree().setSortDirection(comparator.getDirection());
					viewer.getTree().setSortColumn(column);
					viewer.refresh();
				}
			};
		return selectionAdapter;
	}

	public void refresh(boolean refreshLabel)
	{
		contextDataViewer.refresh(refreshLabel);
	}

}
