package junitjndi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junitjndi.contexts.SimpleInitialContext;
import junitjndi.rules.JndiRule;

import org.junit.ClassRule;
import org.junit.Test;

public class JUnitJndiForJBossTest {
	
	@ClassRule
	public static JndiRule jndi = new JndiRule() {
		@Override
		protected void bind(Context context) throws NamingException {
			System.setProperty(SimpleInitialContext.JBOSS_SPECIFIC_KEY, "true");
			context.bind("someobj", new Object());
			context.bind("somestring", "abc");
			context.bind("java:/comp/env/jdbc/mysql", "MysqlDatasource");
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
		assertThat((String)new InitialContext().lookup("comp/env/jdbc/mysql"), is("MysqlDatasource"));
	}
	
	@Test(expected=NamingException.class)
	public void lookupNothing() throws NamingException {
		new InitialContext().lookup("ohter");
	}

}