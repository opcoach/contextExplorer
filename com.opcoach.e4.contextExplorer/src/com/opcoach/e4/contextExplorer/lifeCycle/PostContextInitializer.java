package com.opcoach.e4.contextExplorer.lifeCycle;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

public class PostContextInitializer
{

	@PostContextCreate
	public void initialiseContext(IEclipseContext ctx)
	{
		System.out.println("Update context when PostContextCreate");
		ctx.set(getClass().getName(), this);
	}
	
}
