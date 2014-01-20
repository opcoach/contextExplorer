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
package org.eclipse.e4.tools.context.spy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.internal.tools.context.spy.ContextDataPart;
import org.eclipse.e4.internal.tools.context.spy.ContextSpyHelper;
import org.eclipse.e4.internal.tools.context.spy.ContextSpyProvider;
import org.eclipse.e4.internal.tools.context.spy.search.ContextRegistry;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/** This class is the main part of the context spy. 
 * This part must be added in an application model to spy the contexts
 * It creates a treeviewer and the context data part listening to context selection
 * This part must be included in E4 application to display the contexts
 * @author olivier
 *
 */
public class ContextSpyPart
{

	private TreeViewer contextTreeViewer;

	@Inject
	private ESelectionService selService;

	private ContextSpyProvider treeContentProvider;


	private ImageRegistry imgReg;

	@Inject
	private ContextRegistry contextRegistry;

	private ContextDataPart contextDataPart;

	
	@Inject public void testInjections(ESelectionService myservice) { }
	
	@Inject
	private void initializeImageRegistry()
	{
		Bundle b = FrameworkUtil.getBundle(this.getClass());
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
		refreshButton.setToolTipText("Refresh the contexts");
		refreshButton.addSelectionListener(new SelectionListener()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					contextTreeViewer.refresh(true);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		Button expandAll = new Button(comp, SWT.FLAT);
		expandAll.setImage(imgReg.get("expandall"));
		expandAll.setToolTipText("Expand context nodes");
		expandAll.addSelectionListener(new SelectionListener()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					contextTreeViewer.expandAll();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		Button collapseAll = new Button(comp, SWT.FLAT);
		collapseAll.setImage(imgReg.get("collapseall"));
		collapseAll.setToolTipText("Collapse context nodes");
		collapseAll.addSelectionListener(new SelectionListener()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					contextTreeViewer.collapseAll();
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
		text.setToolTipText("Highlight the contexts where the contained objects match this string pattern.\n"
				+ "You can use patterns like : *selection*, or *NameOfYourClass*");
		text.addKeyListener(new KeyListener()
			{

				@Override
				public void keyReleased(KeyEvent e)
				{
					contextRegistry.setPattern(text.getText());
					contextTreeViewer.refresh(true);
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
		ignoreCase.setToolTipText("Ignore case in the search pattern");
		ignoreCase.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					contextRegistry.setIgnoreCase(ignoreCase.getSelection());
					contextTreeViewer.refresh(true);
					contextDataPart.refresh(true);
				}
			});

		final Button ignoreWildCards = new Button(comp, SWT.CHECK);
		ignoreWildCards.setText("Ignore WildCards");
		ignoreWildCards.setToolTipText("Ignore wildcards in the search pattern");
		ignoreWildCards.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					contextRegistry.setIgnoreWildCards(ignoreWildCards.getSelection());
					contextTreeViewer.refresh(true);
					contextDataPart.refresh(true);
				}
			});

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL | SWT.V_SCROLL | SWT.H_SCROLL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		

		// TreeViewer on the top
		contextTreeViewer = new TreeViewer(sashForm);
		treeContentProvider = ContextInjectionFactory.make(ContextSpyProvider.class, ctx);
		contextTreeViewer.setContentProvider(treeContentProvider);
		contextTreeViewer.setLabelProvider(treeContentProvider);
		contextTreeViewer.setSorter(new ViewerSorter());

		// tv.setInput(a);
		contextTreeViewer.setInput(ContextSpyHelper.getAllBundleContexts());

		contextTreeViewer.addSelectionChangedListener(new ISelectionChangedListener()
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

		// Set the correct weight for SashForm
		sashForm.setWeights(new int[] { 35, 65 });

		// Open all the tree
		contextTreeViewer.expandAll();

	}


	@PreDestroy
	public void dispose()
	{
	}

	@Focus
	public void setFocus()
	{
		contextTreeViewer.getControl().setFocus();
	}

}
