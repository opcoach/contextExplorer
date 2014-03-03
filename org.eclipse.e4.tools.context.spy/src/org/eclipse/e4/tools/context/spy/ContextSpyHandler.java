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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class ContextSpyHandler
{

	@Execute
	public void run(EPartService ps)
	{
		ps.showPart(ContextSpyPart.CONTEXT_SPY_VIEW_DESC, PartState.ACTIVATE);
	}
}
