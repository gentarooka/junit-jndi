package junitjndi.contexts;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class MockNamingEnumeration implements NamingEnumeration<NameClassPair>
{
	private Iterator<NameClassPair> iter;

	public MockNamingEnumeration(final Set<NameClassPair> result)
	{
		this.iter = new LinkedHashSet<NameClassPair>(result).iterator();
	}

	@Override
	public boolean hasMoreElements()
	{
		return iter.hasNext();
	}

	@Override
	public NameClassPair nextElement()
	{
		return iter.next();
	}

	@Override
	public NameClassPair next() throws NamingException
	{
		return iter.next();
	}

	@Override
	public boolean hasMore() throws NamingException
	{
		return iter.hasNext();
	}

	@Override
	public void close() throws NamingException
	{
		iter = null;
	}
}