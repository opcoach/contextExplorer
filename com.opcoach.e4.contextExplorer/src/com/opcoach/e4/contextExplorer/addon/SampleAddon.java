package com.opcoach.e4.contextExplorer.addon;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.core.contexts.IEclipseContext;

public class SampleAddon
{

	@PostConstruct
	void publishInContext(IEclipseContext ctx)
	{
		// Adding this instance in the context (useless, but just for sample)
		ctx.set(getClass().getName(), this);
	}

	@PreDestroy
	void unpublishInContext(IEclipseContext ctx)
	{
		ctx.remove(getClass().getName());
	}
}