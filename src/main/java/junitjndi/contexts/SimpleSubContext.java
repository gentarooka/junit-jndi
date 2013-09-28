package junitjndi.contexts;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

class SimpleSubContext extends NotImplementedContext implements Context{
	private final Hashtable<String, Object> container = new Hashtable<String, Object>();
	
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

}
