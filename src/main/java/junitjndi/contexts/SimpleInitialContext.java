package junitjndi.contexts;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import junitjndi.model.JndiEntryResolver;
import junitjndi.types.JNDINamespace;

import org.apache.commons.lang3.StringUtils;

public class SimpleInitialContext extends NotImplementedContext implements Context
{
	private static final Map<JNDINamespace, Map<String, Object>> DICTIONNARIES = new HashMap<JNDINamespace, Map<String, Object>>(
		JNDINamespace.values().length);

	private static final Map<JNDINamespace, Set<String>> SUBCONTEXTS = new HashMap<JNDINamespace, Set<String>>(JNDINamespace.values().length);

	static
	{
		for (JNDINamespace tje : JNDINamespace.values())
		{
			DICTIONNARIES.put(tje, new HashMap<String, Object>());
			SUBCONTEXTS.put(tje, new TreeSet<String>());
		}
	}

	private final JNDINamespace currentEntry;

	private final String currentSubContext;

	public SimpleInitialContext()
	{
		this(null, null);
	}

	public SimpleInitialContext(final JNDINamespace currentEntry, final String currentSubContext)
	{
		super();
		this.currentEntry = currentEntry;
		this.currentSubContext = currentSubContext;
	}


	@Override
	public Object lookup(Name name) throws NamingException
	{
		return lookup(name.toString());
	}

	@Override
	public Object lookup(String name) throws NamingException
	{
		final JndiEntryResolver jndiEntryResolver = new JndiEntryResolver(name, currentEntry, currentSubContext);

		if (!DICTIONNARIES.get(jndiEntryResolver.getJndiType()).containsKey(jndiEntryResolver.getResolvedName()))
		{
			throw new NamingException("any object is not binded to name : " + jndiEntryResolver.getOriginalName());
		}
		
		return DICTIONNARIES.get(jndiEntryResolver.getJndiType()).get(jndiEntryResolver.getResolvedName());
	}


	@Override
	public void bind(Name name, Object obj) throws NamingException
	{
		bind(name.toString(), obj);
	}

	@Override
	public void bind(String name, Object obj) throws NamingException
	{
		final JndiEntryResolver jndiEntryResolver = new JndiEntryResolver(name, currentEntry, currentSubContext);

		if (DICTIONNARIES.get(jndiEntryResolver.getJndiType()).containsKey(jndiEntryResolver.getResolvedName()))
		{
			throw new NameAlreadyBoundException("Jndi name already exists (" + jndiEntryResolver.getFullQualifiedName() + ").");
		}

		// if the entry is : "java:global/a/b/c/d", we must extract : "a/b/c" to ensure subcontexts exist.
		if (StringUtils.countMatches(jndiEntryResolver.getResolvedName(), "/") > 1)
		{
			final String[] subContextes = StringUtils.split(jndiEntryResolver.getResolvedName(), "/");

			String currentSubContext = "";
			for (int index = 0, size = subContextes.length - 1; index < size; index++)
			{
				if (StringUtils.isBlank(currentSubContext))
				{
					currentSubContext = subContextes[index];
				}
				else
				{
					currentSubContext += "/" + subContextes[index];
				}

				if (!SUBCONTEXTS.get(jndiEntryResolver.getJndiType()).contains(currentSubContext))
				{
					throw new NamingException("SubContext " + currentSubContext + " doesn't exists...");
				}
			}
		}

		DICTIONNARIES.get(jndiEntryResolver.getJndiType()).put(jndiEntryResolver.getResolvedName(), obj);
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		return createSubcontext(name.toString());
	}

	@Override
	public Context createSubcontext(String name) throws NamingException
	{
		final JndiEntryResolver jndiEntryResolver = new JndiEntryResolver(name, currentEntry, currentSubContext);
		final Context subContext = new SimpleInitialContext(jndiEntryResolver.getJndiType(), jndiEntryResolver.getResolvedName());
		SUBCONTEXTS.get(jndiEntryResolver.getJndiType()).add(jndiEntryResolver.getResolvedName());
		return subContext;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException
	{
		return list(name.toString());
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException
	{
		final JndiEntryResolver jndiEntryResolver = new JndiEntryResolver(name, currentEntry, currentSubContext);

		if (!SUBCONTEXTS.get(jndiEntryResolver.getJndiType()).contains(jndiEntryResolver.getResolvedName()))
		{
			throw new NamingException("any object is not binded to name : " + jndiEntryResolver.getFullQualifiedName());
		}

		final Set<NameClassPair> result = new LinkedHashSet<NameClassPair>(DICTIONNARIES.get(jndiEntryResolver.getJndiType()).size());
		for (Entry<String, Object> entry : DICTIONNARIES.get(jndiEntryResolver.getJndiType()).entrySet())
		{
			if (StringUtils.startsWith(entry.getKey(), jndiEntryResolver.getResolvedName() + "/"))
			{
				final String className = entry.getValue() == null ? null : entry.getValue().getClass().toString();
				result.add(new NameClassPair(jndiEntryResolver.getJndiType().getJndiEntry() + entry.getKey(), className, false));
			}
		}

		return new MockNamingEnumeration(result);
	}

	public boolean isRootContext()
	{
		return this.currentEntry == null && StringUtils.isBlank(this.currentSubContext);
	}

	public boolean isSubContext()
	{
		return !isRootContext();
	}

	public JNDINamespace getCurrentEntry()
	{
		return currentEntry;
	}

	public String getCurrentSubContext()
	{
		return currentSubContext;
	}
}