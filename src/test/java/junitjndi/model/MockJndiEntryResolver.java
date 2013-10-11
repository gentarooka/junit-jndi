package junitjndi.model;

import javax.annotation.Nullable;
import javax.naming.NamingException;

import junitjndi.types.JNDINamespace;

public class MockJndiEntryResolver extends JndiEntryResolver
{
	public MockJndiEntryResolver(String originalName) throws NamingException
	{
		this(originalName, null, null);
	}

	public MockJndiEntryResolver(@Nullable final String originalName, @Nullable JNDINamespace parentJndiEntry, @Nullable String originalParentName)
		throws NamingException
	{
		super(originalName, parentJndiEntry, originalParentName);
	}

	@Override
	protected void validate() throws NamingException
	{
		// ne rien faire...
	}
}
