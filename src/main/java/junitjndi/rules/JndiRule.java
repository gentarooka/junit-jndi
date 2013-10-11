package junitjndi.rules;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junitjndi.JUnitJndiInitialContextFactory;
import junitjndi.contexts.SimpleInitialContext;

import org.junit.rules.ExternalResource;

public class JndiRule extends ExternalResource{

	@Override
	protected final void before() throws Throwable {
		System.setProperty("java.naming.factory.initial", JUnitJndiInitialContextFactory.class.getName());
		System.setProperty(SimpleInitialContext.JBOSS_SPECIFIC_KEY, "false");
		bind(new InitialContext());
	}

	@Override
	protected final void after() {
		
	}
	
	protected void bind(Context context) throws NamingException {
		// this method should be overrided to initialize context
	}
}
