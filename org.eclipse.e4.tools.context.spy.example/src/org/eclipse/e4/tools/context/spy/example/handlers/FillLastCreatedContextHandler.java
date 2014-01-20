 
package org.eclipse.e4.tools.context.spy.example.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;


public class FillLastCreatedContextHandler {
	
	 static IEclipseContext lastCreatedContext;
		
	@Execute
	public void execute(MApplication appli) {
		
	  // Fill the last created context with a set of values
		lastCreatedContext.set("org.eclipse.e4.tools.context.spy.example", "Sample Data");
		lastCreatedContext.set(IEclipseContext.class, lastCreatedContext);
	}
		
}