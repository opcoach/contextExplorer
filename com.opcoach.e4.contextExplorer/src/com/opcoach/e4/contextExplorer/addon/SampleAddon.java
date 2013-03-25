

package com.opcoach.e4.contextExplorer.addon;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;

public class SampleAddon {
	@Inject
	IEventBroker eventBroker;
	
	@PostConstruct
	void publishInContext(IEclipseContext ctx) {
		ctx.set(getClass().getName(), this);
	}
	
	@PreDestroy
	void unhookListeners(IEclipseContext ctx) {
		ctx.remove(getClass().getName());
	}
}