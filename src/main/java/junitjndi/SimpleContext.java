package junitjndi;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class SimpleContext implements Context {
	private static final Hashtable<String, Object> container = new Hashtable<String, Object>();

	@Override
	public Object lookup(Name name) throws NamingException {
		Object result = container.get(name.toString());
		
		if (result == null) {
			throw new NamingException("any object is not binded to name : " + name.toString());
		}
		
		return result;
	}

	@Override
	public Object lookup(String name) throws NamingException {
		Object result = container.get(name);
		
		if (result == null) {
			throw new NamingException("any object is not binded to name : " + name.toString());
		}
		
		return result;
	}

	@Override
	public void bind(Name name, Object obj) throws NamingException {
		container.put(name.toString(), obj);
	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		container.put(name, obj);
	}

	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rebind(String name, Object obj) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unbind(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unbind(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		throw new UnsupportedOperationException();

	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String composeName(String name, String prefix)
			throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		throw new UnsupportedOperationException();
	}

}
