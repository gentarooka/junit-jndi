package junitjndi.model;

import static org.fest.assertions.api.Assertions.assertThat;

import javax.naming.NamingException;

import org.junit.Test;

import junitjndi.types.JNDINamespace;

public class JndiEntryResolverTest
{
	@Test
	public void testConstructorSimpleWithNullValue() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver(null);
		assertThat(resolver).isNotNull();
		assertThat(resolver.getOriginalParentName()).isNull();
		assertThat(resolver.getOriginalName()).isNull();
		assertThat(resolver.getJndiType()).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.getResolvedName()).isEqualTo("");
		assertThat(resolver.getFullQualifiedName()).isEqualTo("java:/");
	}

	@Test
	public void testConstructorSimpleWithEmptyValue() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver("");
		assertThat(resolver).isNotNull();
		assertThat(resolver.getOriginalParentName()).isNull();
		assertThat(resolver.getOriginalName()).isEqualTo("");
		assertThat(resolver.getJndiType()).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.getResolvedName()).isEqualTo("");
		assertThat(resolver.getFullQualifiedName()).isEqualTo("java:/");
	}

	@Test
	public void testConstructorSimpleWithJustCategoryRoot() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver("java:/");
		assertThat(resolver).isNotNull();
		assertThat(resolver.getOriginalParentName()).isNull();
		assertThat(resolver.getOriginalName()).isEqualTo("java:/");
		assertThat(resolver.getJndiType()).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.getResolvedName()).isEqualTo("");
		assertThat(resolver.getFullQualifiedName()).isEqualTo("java:/");
	}

	@Test
	public void testConstructorSimpleWithJustCategoryApplication() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver("java:app/");
		assertThat(resolver).isNotNull();
		assertThat(resolver.getOriginalParentName()).isNull();
		assertThat(resolver.getOriginalName()).isEqualTo("java:app/");
		assertThat(resolver.getJndiType()).isEqualTo(JNDINamespace.APPLICATION);
		assertThat(resolver.getResolvedName()).isEqualTo("");
		assertThat(resolver.getFullQualifiedName()).isEqualTo("java:app/");
	}

	@Test
	public void testConstructorSimpleWithSimpleValue() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver("allo");
		assertThat(resolver).isNotNull();
		assertThat(resolver.getOriginalParentName()).isNull();
		assertThat(resolver.getOriginalName()).isEqualTo("allo");
		assertThat(resolver.getJndiType()).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.getResolvedName()).isEqualTo("allo");
		assertThat(resolver.getFullQualifiedName()).isEqualTo("java:/allo");
	}

	@Test
	public void testConstructorSimpleWithSimpleValueAndSlash() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver("/allo");
		assertThat(resolver).isNotNull();
		assertThat(resolver.getOriginalParentName()).isNull();
		assertThat(resolver.getOriginalName()).isEqualTo("/allo");
		assertThat(resolver.getJndiType()).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.getResolvedName()).isEqualTo("allo");
		assertThat(resolver.getFullQualifiedName()).isEqualTo("java:/allo");
	}

	@Test
	public void testConstructorSimpleWithValueAndVisibilityGlobal() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver("java:global/bonjour/fabien");
		assertThat(resolver).isNotNull();
		assertThat(resolver.getOriginalParentName()).isNull();
		assertThat(resolver.getOriginalName()).isEqualTo("java:global/bonjour/fabien");
		assertThat(resolver.getJndiType()).isEqualTo(JNDINamespace.GLOBAL);
		assertThat(resolver.getResolvedName()).isEqualTo("bonjour/fabien");
		assertThat(resolver.getFullQualifiedName()).isEqualTo("java:global/bonjour/fabien");
	}

	@Test
	public void testConstructorComplexWithAllValues() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver("/bravo/navette", JNDINamespace.JBOSS, "niv1/niv2");
		assertThat(resolver).isNotNull();
		assertThat(resolver.getOriginalParentName()).isEqualTo("niv1/niv2");
		assertThat(resolver.getOriginalName()).isEqualTo("/bravo/navette");
		assertThat(resolver.getJndiType()).isEqualTo(JNDINamespace.JBOSS);
		assertThat(resolver.getResolvedName()).isEqualTo("niv1/niv2/bravo/navette");
		assertThat(resolver.getFullQualifiedName()).isEqualTo("java:jboss/niv1/niv2/bravo/navette");
	}

	@Test
	public void testConstructorComplexWithAllValuesGlobal() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver("java:app/puf", JNDINamespace.GLOBAL, "a/b/c/d/e/f");
		assertThat(resolver).isNotNull();
		assertThat(resolver.getOriginalParentName()).isEqualTo("a/b/c/d/e/f");
		assertThat(resolver.getOriginalName()).isEqualTo("java:app/puf");
		assertThat(resolver.getJndiType()).isEqualTo(JNDINamespace.GLOBAL);
		assertThat(resolver.getResolvedName()).isEqualTo("a/b/c/d/e/f/puf");
		assertThat(resolver.getFullQualifiedName()).isEqualTo("java:global/a/b/c/d/e/f/puf");
	}

	@Test()
	public void testDetectJndiEntryType() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver(null);

		assertThat(resolver.detectJndiEntryType(null)).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.detectJndiEntryType("")).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.detectJndiEntryType("pof")).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.detectJndiEntryType("java:global")).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.detectJndiEntryType("java:global/")).isEqualTo(JNDINamespace.GLOBAL);
		assertThat(resolver.detectJndiEntryType("java:global/allo")).isEqualTo(JNDINamespace.GLOBAL);
		assertThat(resolver.detectJndiEntryType("java:app/allo")).isEqualTo(JNDINamespace.APPLICATION);
		assertThat(resolver.detectJndiEntryType("java:app/allo/maman/bobo")).isEqualTo(JNDINamespace.APPLICATION);
		assertThat(resolver.detectJndiEntryType("java:module/allo/alhuile")).isEqualTo(JNDINamespace.MODULE);
		assertThat(resolver.detectJndiEntryType("java:jboss/toc")).isEqualTo(JNDINamespace.JBOSS);
		assertThat(resolver.detectJndiEntryType("java:/a/b/c/d")).isEqualTo(JNDINamespace.ROOT);
		assertThat(resolver.detectJndiEntryType("java:comp/x/y/z")).isEqualTo(JNDINamespace.COMP);
		assertThat(resolver.detectJndiEntryType("java:jboss/zz/qq")).isEqualTo(JNDINamespace.JBOSS);
	}

	@Test()
	public void testDetectResolvedName() throws Exception
	{
		final JndiEntryResolver resolver = new MockJndiEntryResolver(null);
		assertThat(resolver.detectResolvedName("allo", JNDINamespace.ROOT, null)).isEqualTo("allo");
		assertThat(resolver.detectResolvedName("/allo", JNDINamespace.JBOSS, "/level1/level2/level3")).isEqualTo("level1/level2/level3/allo");
		assertThat(resolver.detectResolvedName(null, JNDINamespace.MODULE, null)).isEqualTo("");
		assertThat(resolver.detectResolvedName("", JNDINamespace.MODULE, null)).isEqualTo("");
		assertThat(resolver.detectResolvedName("", JNDINamespace.MODULE, "")).isEqualTo("");
		assertThat(resolver.detectResolvedName("", JNDINamespace.MODULE, "abc")).isEqualTo("abc/");
	}

	@Test(expected = NamingException.class)
	public void testValidateJndiNameNull() throws Exception
	{
		new JndiEntryResolver(null);
	}

	@Test(expected = NamingException.class)
	public void testValidateJndiNameEmpty() throws Exception
	{
		new JndiEntryResolver("");
	}

	@Test(expected = NamingException.class)
	public void testValidateJndiNameBlank() throws Exception
	{
		new JndiEntryResolver("  ");
	}

	@Test(expected = NamingException.class)
	public void testValidateWithJndiNameDoubleSlash() throws Exception
	{
		new JndiEntryResolver("//webapp");
	}

	@Test(expected = NamingException.class)
	public void testValidateWithJndiNameEndSlash() throws Exception
	{
		new JndiEntryResolver("/webapp/");
	}

	@Test(expected = NamingException.class)
	public void testValidateWithJndiNameContainDoubleSlash() throws Exception
	{
		new JndiEntryResolver("/web//app");
	}

	@Test()
	public void testValidate() throws Exception
	{
		new JndiEntryResolver("/webapp");
		new JndiEntryResolver("webapp");
		new JndiEntryResolver("java:module/webapp");
		new JndiEntryResolver("java:/webapp");
	}

}
