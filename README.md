# JNDI for JUnit4

this library is JNDI mock implementation (compatible java EE6 specifications) for testing with JUnit4.x
This API implements a jboss specific mode to be compatible with jboss as 7.x
You can activate this mode if the SimpleInitialContext.JBOSS_SPECIFIC_KEY system property is equal to "true".

# USAGE

## INSTALL

    $ git clone git://github.com/gensan/junit-jndi.git junit-jndi
    $ cd junit-jndi
    $ mvn install

and write dependency in your pom.xml

    <dependency>
      <groupId>junit-jndi</groupId>
      <artifactId>junit-jndi</artifactId>
      <version>2.0-SNAPSHOT</version>
    </dependency>


## HOW TO WRITE TESTCASE WITH JNDI-JUNIT

It's very simple to use.

Please see JndiJunitTest.java for more details.

    public class JUnitJndiTest {
    
    	@ClassRule
    	public static JndiRule jndi = new JndiRule() {
    		@Override
    		protected void bind(Context context) throws NamingException {
    			context.bind("someobj", new Object());
    			context.bind("somestring", "abc");
    			
    			// by default, the java:/ namespace is used.
    			context.bind("/level1/level2", "abc");
    			
    			// we can create entries on global namespace.
    			context.bind("java:global/date/us", "2013-10-01");
    			context.bind("java:global/date/fr", "01/10/2013");
    			
    			// with many levels, you must create subcontextes.
    			context.createSubContext("java:app/level1").createSubContext("level2").createSubContext("level3");
    			context.bind("java:app/level1/level2/level3/title", "example");
    			
    		}
    	};
    
    	@Test
    	public void lookup() throws NamingException {
    		final Context ic = new InitialContext();
    		
    		assertThat(ic.lookup("someobj"), is(notNullValue(Object.class)));
    		assertThat((String)ic.lookup("somestring"), is("abc"));
    		
    		assertThat((String)ic.lookup("level1/level2"), is("abc"));
    		assertThat((String)ic.lookup("/level1/level2"), is("abc"));
    		assertThat((String)ic.lookup("java:/level1/level2"), is("abc"));
    		
    		assertThat((String)ic.lookup("java:global/date/us"), is("2013-10-01"));
    		assertThat((String)ic.lookup("java:global/date/fr"), is("01/10/2013"));
    		
    		assertThat((String)ic.lookup("java:app/level1/level2/level3/title"), is("example"));
    	}
    
    }

# LICENSE

* [MIT License](http://www.opensource.org/licenses/mit-license.php)
