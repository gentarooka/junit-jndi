package junitjndi.contexts;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public class SimpleInitialContext extends NotImplementedContext implements Context
{
	private static final Hashtable<String, Object> container = new Hashtable<String, Object>();

	@Override
	public Object lookup(Name name) throws NamingException {
		return lookup(name.toString());
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
		bind(name.toString(), obj);
	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		container.put(name, obj);
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		return createSubcontext(name.toString());
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		Context cx = new SimpleSubContext();
		container.put(name.toString(), cx);
		return cx;
	}
}
