package junitjndi.contexts;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import junitjndi.types.JNDINamespace;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class SimpleInitialContextTest
{
	@Before
	public void before()
	{
		SimpleInitialContext.reset();
		System.setProperty(SimpleInitialContext.JBOSS_SPECIFIC_KEY, "false");
	}

	@Test()
	public void testValueForJBossSpecificMode() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		assertThat(sc.isJBossSpecificMode()).isEqualTo(false);
	}

	@Test()
	public void testBindSimple() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		final Map<String, Object> mapDataSets = new LinkedHashMap<String, Object>();
		mapDataSets.put("env", "tutu");
		mapDataSets.put("java:global/pof", "pof");
		mapDataSets.put("java:global/puf", "pif");
		mapDataSets.put("java:app/puf", "paf");
		mapDataSets.put("java:jboss/puf", "prrr");
		mapDataSets.put("java:comp/puf", "tata");
		mapDataSets.put("java:module/puf", null);

		for (Entry<String, Object> entry : mapDataSets.entrySet())
		{
			sc.bind(entry.getKey(), entry.getValue());
			assertThat(sc.lookup(entry.getKey())).isEqualTo(entry.getValue());
		}

		sc.bind("titi", 1L);
		assertThat(sc.lookup("java:/titi")).isEqualTo(1L);

		sc.bind("/fabien", 100L);
		assertThat(sc.lookup("java:/fabien")).isEqualTo(100L);
	}

	@Test(expected = NamingException.class)
	public void testBindWithoutSubContext() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		sc.bind("java:/un/deux/trois", "exemple");
	}

	@Test(expected = NamingException.class)
	public void testLookupOnKeyNotFound() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		sc.lookup("java:/un/deux/trois");
	}

	@Test()
	public void testBindThreeLevelsOnRootContext() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();

		final SimpleInitialContext subContext1 = (SimpleInitialContext)sc.createSubcontext("/aaa");
		assertThat(subContext1).isNotNull();
		assertThat(subContext1.getCurrentEntry()).isEqualTo(JNDINamespace.ROOT);
		assertThat(subContext1.getCurrentSubContext()).isEqualTo("aaa");

		final SimpleInitialContext subContext2 = (SimpleInitialContext)subContext1.createSubcontext("/bbb");
		assertThat(subContext2).isNotNull();
		assertThat(subContext2.getCurrentEntry()).isEqualTo(JNDINamespace.ROOT);
		assertThat(subContext2.getCurrentSubContext()).isEqualTo("aaa/bbb");

		final SimpleInitialContext subContext3 = (SimpleInitialContext)subContext2.createSubcontext("java:global/ccc");
		assertThat(subContext3).isNotNull();
		assertThat(subContext3.getCurrentEntry()).isEqualTo(JNDINamespace.ROOT);
		assertThat(subContext3.getCurrentSubContext()).isEqualTo("aaa/bbb/ccc");

		sc.bind("/aaa/bbb/ccc/ddd", "alloQuoiRoot");
		assertThat(sc.lookup("aaa/bbb/ccc/ddd")).isEqualTo("alloQuoiRoot");

		assertThat(sc.lookup("/aaa")).isInstanceOf(Context.class);
		assertThat(sc.lookup("/aaa/bbb")).isInstanceOf(Context.class);
		assertThat(sc.lookup("/aaa/bbb/ccc")).isInstanceOf(Context.class);
	}

	@Test(expected = NameAlreadyBoundException.class)
	public void testBindObjectONSubContext() throws Exception
	{
		// we can't bind an object if a subcontext with the same name has been created...
		final SimpleInitialContext sc = new SimpleInitialContext();
		final SimpleInitialContext subContext = (SimpleInitialContext)sc.createSubcontext("/agileTourToulouse");
		sc.bind("/agileTourToulouse", "princess");
	}

	@Test()
	public void testBindComplex() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();

		final SimpleInitialContext subContext1 = (SimpleInitialContext)sc.createSubcontext("java:app/niv1");
		validateSubContext(subContext1, JNDINamespace.APPLICATION, "niv1");

		final SimpleInitialContext subContext2 = (SimpleInitialContext)subContext1.createSubcontext("niv2");
		validateSubContext(subContext2, JNDINamespace.APPLICATION, "niv1/niv2");

		final SimpleInitialContext subContext3 = (SimpleInitialContext)subContext2.createSubcontext("niv3");
		validateSubContext(subContext3, JNDINamespace.APPLICATION, "niv1/niv2/niv3");

		final SimpleInitialContext subContext4 = (SimpleInitialContext)subContext3.createSubcontext("niv4");
		validateSubContext(subContext4, JNDINamespace.APPLICATION, "niv1/niv2/niv3/niv4");

		sc.bind("java:app/niv1/niv2/niv3/niv4/monExemple", "exemple");
		assertThat(sc.lookup("java:app/niv1/niv2/niv3/niv4/monExemple")).isEqualTo("exemple");
	}

	@Test(expected = NamingException.class)
	public void testListNotfound() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		sc.list("java:global/mock/mock2/mock3/mock4");
	}

	@Test()
	public void testList() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		sc.createSubcontext("java:global/zzzz").createSubcontext("qqqq").createSubcontext("iiii");

		final Map<String, Object> dataSets = new HashMap<String, Object>(10);
		dataSets.put("zzzz/qqqq/iiii/v1", "allo");
		dataSets.put("zzzz/qqqq/iiii/v2", "maman");
		dataSets.put("zzzz/qqqq/iiii/v3", "bobo");
		dataSets.put("zzzz/qqqq/iiii/v4", "je");
		dataSets.put("zzzz/qqqq/iiii/v5", "t'appelle");
		dataSets.put("zzzz/qqqq/iiii/v6", "parce que je suis pas beau !");

		for (Entry<String, Object> entry : dataSets.entrySet())
		{
			final String key = "java:global/" + entry.getKey();
			sc.bind(key, entry.getValue());
			assertThat(sc.lookup(key)).isEqualTo(entry.getValue());
		}

		final NamingEnumeration<NameClassPair> namingEnum = sc.list("java:global/zzzz/qqqq/iiii");
		int count = 0;
		while (namingEnum.hasMore())
		{
			final NameClassPair ncp = namingEnum.next();
			count++;

			assertThat(ncp).isNotNull();
			assertThat(ncp.getName()).isNotNull();
			assertThat(sc.lookup(ncp.getName())).isEqualTo(dataSets.get(StringUtils.substringAfter(ncp.getName(), "/")));
		}

		assertThat(count).isEqualTo(dataSets.size());
	}

	@Test(expected = NameAlreadyBoundException.class)
	public void testMultipleSameSubContexts() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		sc.createSubcontext("java:global/allo");
		sc.createSubcontext("java:global/allo");
	}

	@Test
	public void testUnbind() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		sc.bind("java:global/niv1", "puf");
		sc.bind("java:global/niv1/niv2", "puf");

		sc.unbind("java:global/niv1");
		sc.unbind("java:global/niv1/niv2");
	}

	private void validateSubContext(final SimpleInitialContext subContext, final JNDINamespace namespace, final String pathExpected)
	{
		assertThat(subContext).isNotNull();
		assertThat(subContext.getCurrentEntry()).isEqualTo(namespace);
		assertThat(subContext.getCurrentSubContext()).isEqualTo(pathExpected);
	}
}