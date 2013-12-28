 
package com.opcoach.e4.contextExplorer.test.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;


public class FillLastCreatedContext {
	
	 static IEclipseContext lastCreatedContext;
		
	@Execute
	public void execute(MApplication appli) {
		
	  // Fill the last created context with a set of values
		lastCreatedContext.set("com.opcoach.e4.contextExplorer.test.sample", "Sample");
		lastCreatedContext.set(IEclipseContext.class, lastCreatedContext);
	}
		
}