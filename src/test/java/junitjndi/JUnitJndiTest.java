package junitjndi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.ClassRule;
import org.junit.Test;

public class JUnitJndiTest {
	
	@ClassRule
	public static JndiRule jndi = new JndiRule() {
		@Override
		protected void bind(Context context) throws NamingException {
			context.bind("someobj", new Object());
			context.bind("somestring", "abc");
			
			Context cx = context.createSubcontext("java:/comp/env");
			cx.bind("jdbc/mysql", "MysqlDatasource");
		}
	};
	
	
	@Test
	public void lookup() throws NamingException {
		assertThat(new InitialContext().lookup("someobj"), is(notNullValue(Object.class)));
		assertThat((String)new InitialContext().lookup("somestring"), is("abc"));
	}
	
	@Test
	public void doLookup() throws NamingException {
		assertThat(InitialContext.doLookup("someobj"), is(notNullValue(Object.class)));
		assertThat((String)InitialContext.doLookup("somestring"), is("abc"));
	}
	
	@Test
	public void lookupSubContext() throws NamingException {
		Context cx = (Context) new InitialContext().lookup("java:/comp/env");
		assertThat((String)cx.lookup("jdbc/mysql"), is("MysqlDatasource"));
	}
	
	@Test(expected=NamingException.class)
	public void lookupNothing() throws NamingException {
		new InitialContext().lookup("ohter");
	}

}