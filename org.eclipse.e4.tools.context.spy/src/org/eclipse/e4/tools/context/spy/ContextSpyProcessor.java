/*******************************************************************************
 * Copyright (c) 2014 OPCoach.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olivier Prouvost <olivier.prouvost@opcoach.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.context.spy;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MBindingContext;
import org.eclipse.e4.ui.model.application.commands.MBindingTable;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.model.application.commands.MKeyBinding;
import org.eclipse.e4.ui.model.application.descriptor.basic.MPartDescriptor;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class ContextSpyProcessor
{
	private static final String ORG_ECLIPSE_UI_CONTEXTS_DIALOG_AND_WINDOW = "org.eclipse.ui.contexts.dialogAndWindow";
	private static final String CONTEXT_SPY_SHORTCUT = "M2+M3+F10";
	private static final String E4_SPIES_BINDING_TABLE = "e4.tooling.spies.bindings";
	private static final String CONTEXT_SPY_HANDLER = "e4.tooling.context.spy.handler";
	private static final String CONTEXT_SPY_COMMAND = "e4.tooling.context.spy.command";
	private static final String CONTEXT_SPY_CONTRIBUTOR_URI = "platform:/plugin/org.eclipse.e4.tools.context.spy";

	@Inject
	MApplication application;
	@Inject
	EModelService modelService;

	@PostConstruct
	public void process()
	{
		// This processor must :
		// 1. Add the e4.tooling.context.spy Command, if not present
		// 2. Add the e4.tooling.context.spy.handler Handler, if not present
		// 3. Deal with binding context
		// 4. Add the org.eclipse.e4.tools.context.spy.view part descriptor, if
		// not present
		// Warning : do not use findElement on modelService, it returns only
		// MUIElements

		// ..............................
		// 1. Add the command 
		// ..............................
		MCommand command = null;
		for (MCommand cmd : application.getCommands())
		{
			if (CONTEXT_SPY_COMMAND.equals(cmd.getElementId()))
			{
				command = cmd;
				break;
			}
		}

		if (command == null)
		{
			command = modelService.createModelElement(MCommand.class);
			command.setElementId(CONTEXT_SPY_COMMAND);
			command.setCommandName("Show E4 Context Spy");
			command.setContributorURI(CONTEXT_SPY_CONTRIBUTOR_URI);
			command.setDescription("Show the Eclipse 4 contexts and their contents");
			application.getCommands().add(command);
		}

		// ..............................
		// 2. Add the handler
		// ..............................
		MHandler handler = null;
		for (MHandler hdl : application.getHandlers())
		{
			if (CONTEXT_SPY_HANDLER.equals(hdl.getElementId()))
			{
				handler = hdl;
				break;
			}
		}

		if (handler == null)
		{
			handler = modelService.createModelElement(MHandler.class);
			handler.setElementId(CONTEXT_SPY_HANDLER);
			handler.setContributionURI("bundleclass://org.eclipse.e4.tools.context.spy/org.eclipse.e4.tools.context.spy.ContextSpyHandler");
			handler.setContributorURI(CONTEXT_SPY_CONTRIBUTOR_URI);
			application.getHandlers().add(handler);
		}

		handler.setCommand(command);

		// ..............................
		// 3. Deal with key bindings
		// ..............................
		bindSpyKeyBinding(CONTEXT_SPY_SHORTCUT, command);

		// ..............................
		// 4. Add part descriptor
		// ..............................
		MPartDescriptor descriptor = null;
		for (MPartDescriptor mp : application.getDescriptors())
		{
			if (ContextSpyPart.CONTEXT_SPY_VIEW_DESC.equals(mp.getElementId()))
			{
				descriptor = mp;
				break;
			}
		}

		// If descriptor not yet in descriptor list, add it now
		if (descriptor == null)
		{
			descriptor = modelService.createModelElement(MPartDescriptor.class);
			descriptor.setCategory("org.eclipse.e4.secondaryDataStack");
			descriptor.setElementId(ContextSpyPart.CONTEXT_SPY_VIEW_DESC);
			descriptor.getTags().add("View");
			descriptor.getTags().add("categoryTag:General");

			descriptor.setLabel("Context Spy");
			descriptor
					.setContributionURI("bundleclass://org.eclipse.e4.tools.context.spy/org.eclipse.e4.tools.context.spy.ContextSpyPart");
			descriptor.setContributorURI(CONTEXT_SPY_CONTRIBUTOR_URI);
			descriptor.setIconURI(CONTEXT_SPY_CONTRIBUTOR_URI + "/icons/annotation_obj.gif");
			application.getDescriptors().add(descriptor);
		}

	}

	/**
	 * Helper method to get or create the binding table for all spies (where
	 * spies will add their key binding). Bind this table with the
	 * org.eclipse.ui.contexts.dialogAndWindow binding context which should be
	 * present (create it if not)
	 * 
	 * This method will probably move to the common spy plugin providing common
	 * spy stuff (see bug #428903)
	 * 
	 * @param application
	 * @return
	 */
	public void bindSpyKeyBinding(String keySequence, MCommand cmd)
	{
		// This method must :
		// search for a binding table having the binding context 'dialog and
		// window'
		// If none found, create it and also the binding context
		// Then can add the KeyBinding if not already added

		MBindingTable spyBindingTable = null;
		for (MBindingTable bt : application.getBindingTables())
			if (ORG_ECLIPSE_UI_CONTEXTS_DIALOG_AND_WINDOW.equals(bt.getBindingContext().getElementId()))
			{
				spyBindingTable = bt;
			}

		// Binding table has not been yet added... Create it and bind it to
		// org.eclipse.ui.contexts.dialogAndWindow binding context
		// If this context does not yet exist, create it also.
		if (spyBindingTable == null)
		{
			MBindingContext dialAndWindowContext = null;
			for (MBindingContext bc : application.getBindingContexts())
				if (ORG_ECLIPSE_UI_CONTEXTS_DIALOG_AND_WINDOW.equals(bc.getElementId()))
				{
					dialAndWindowContext = bc;
					break;
				}

			if (dialAndWindowContext == null)
			{
				// This context has not yet been created... Application model
				// must be very poor....
				dialAndWindowContext = modelService.createModelElement(MBindingContext.class);
				dialAndWindowContext.setElementId(ORG_ECLIPSE_UI_CONTEXTS_DIALOG_AND_WINDOW);
			}

			// Can now create the binding table and bind it to this context...
			spyBindingTable = modelService.createModelElement(MBindingTable.class);
			spyBindingTable.setElementId(E4_SPIES_BINDING_TABLE);
			spyBindingTable.setBindingContext(dialAndWindowContext);
			application.getBindingTables().add(spyBindingTable);
		}

		// Search for the key binding if already present
		for (MKeyBinding kb : spyBindingTable.getBindings())
			if (keySequence.equals(kb.getKeySequence()))
			{
				// A binding with this key sequence is already present. Check if
				// command is the same
				if (kb.getCommand().getElementId().equals(cmd.getElementId()))
					return;
				else
				{
					// Must log an error : key binding already exists in this
					// table but with another command
					System.out.println("WARNING : Cannot bind the command '" + cmd.getElementId() + "' to the keySequence : "
							+ keySequence + " because the command " + kb.getCommand().getElementId() + " is already bound !");
					return;
				}
			}

		// Key binding is not yet in table... can add it now.
		MKeyBinding binding = modelService.createModelElement(MKeyBinding.class);
		binding.setElementId(cmd.getElementId() + ".binding");
		binding.setContributorURI(cmd.getContributorURI());
		binding.setKeySequence(keySequence);
		spyBindingTable.getBindings().add(binding);
		binding.setCommand(cmd);

	}

}
