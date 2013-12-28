package com.opcoach.e4.contextExplorer.test.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;

public class CreateContext {

	private static int index = 0;

	private IEclipseContext lastCreatedContext = null;

	@Execute
	public void execute(MApplication appli) {
		IEclipseContext ctx = appli.getContext().createChild(
				"Context de test " + index);
		ctx.set("data.sample", "sample");
		FillLastCreatedContext.lastCreatedContext = ctx;
		System.out.println("Add a new context " + index);
		index++;
	}
	
	@Inject @Optional
	public void myInjectedMethod( @Named("data.sample") String param)
	{
		System.out.println("Param value is : " + param);
	}

}