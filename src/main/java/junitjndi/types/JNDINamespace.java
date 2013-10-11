package junitjndi.types;

public enum JNDINamespace
{
	ROOT("java:/"),

	GLOBAL("java:global/"),

	APPLICATION("java:app/"),

	MODULE("java:module/"),

	COMP("java:comp/"),

	JBOSS("java:jboss/");

	private final String jndiEntry;

	JNDINamespace(final String jndiEntry)
	{
		this.jndiEntry = jndiEntry;
	}

	public String getJndiEntry()
	{
		return jndiEntry;
	}
}
