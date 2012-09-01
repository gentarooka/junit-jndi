# JNDI for JUnit4

this library is JNDI mock implementation for testing with JUnit4.x

# USAGE

## INSTALL

    $ git clone git://github.com/gensan/junit-jndi.git junit-jndi
    $ cd junit-jndi
    $ mvn install

and write dependency in your pom.xml

    <dependency>
      <groupId>junit-jndi</groupId>
      <artifactId>junit-jndi</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>


## HOW TO WRITE TESTCASE WITH JNDI-JUNIT

It's very simple to use.

Please see JndiJunitTest.java

    public class JUnitJndiTest {
    
    	@ClassRule
    	public static JndiRule jndi = new JndiRule() {
    		@Override
    		protected void bind(Context context) throws NamingException {
    			context.bind("someobj", new Object());
    			context.bind("somestring", "abc");
    		}
    	};
    
    
    	@Test
    	public void lookup() throws NamingException {
    		assertThat(new InitialContext().lookup("someobj"), is(notNullValue(Object.class)));
    		assertThat((String)new InitialContext().lookup("somestring"), is("abc"));
    	}
    
    	@Test(expected=NamingException.class)
    	public void lookupNothing() throws NamingException {
    		new InitialContext().lookup("ohter");
    	}
    
    }

# LICENSE

* [MIT License](http://www.opensource.org/licenses/mit-license.php)
