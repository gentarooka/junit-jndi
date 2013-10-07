package junitjndi.contexts;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import junitjndi.types.JNDINamespace;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class SimpleInitialContextTest
{
	@Test()
	public void testBindSimple() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		final Map<String, Object> mapDataSets = new LinkedHashMap<String, Object>();
		mapDataSets.put("env", "tutu");
		mapDataSets.put("env/muche", "truc");
		mapDataSets.put("java:global/pof", "pof");
		mapDataSets.put("java:global/puf", "pif");
		mapDataSets.put("java:global/tata/titi", "asterix");
		mapDataSets.put("java:app/puf", "paf");
		mapDataSets.put("java:jboss/puf", "prrr");
		mapDataSets.put("java:comp/puf", "tata");
		mapDataSets.put("java:module/puf", "prout");
		mapDataSets.put("java:global/fap/gag", null);
		mapDataSets.put("java:app/allo/papa", "bobo");

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

		// we can bind an object even if a subcontext with same name has been created...
		sc.bind("/aaa", "princess");
		assertThat(sc.lookup("aaa")).isEqualTo("princess");

		sc.bind("/aaa/bbb/ccc", "alloQuoiRoot");
		assertThat(sc.lookup("aaa/bbb/ccc")).isEqualTo("alloQuoiRoot");
		assertThat(sc.lookup("/aaa/bbb/ccc")).isEqualTo("alloQuoiRoot");
		assertThat(sc.lookup("java:/aaa/bbb/ccc")).isEqualTo("alloQuoiRoot");
	}

	@Test()
	public void testBindThreeLevelsOnGlobalContext() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();

		final SimpleInitialContext subContext1 = (SimpleInitialContext)sc.createSubcontext("java:global/aaa");
		validateSubContext(subContext1, JNDINamespace.GLOBAL, "aaa");

		final SimpleInitialContext subContext2 = (SimpleInitialContext)subContext1.createSubcontext("bbb");
		validateSubContext(subContext2, JNDINamespace.GLOBAL, "aaa/bbb");

		final SimpleInitialContext subContext3 = (SimpleInitialContext)subContext2.createSubcontext("java:global/ccc");
		validateSubContext(subContext3, JNDINamespace.GLOBAL, "aaa/bbb/ccc");

		// we can bind an object even if a subcontext with same name has been created...
		sc.bind("java:global/aaa", "princess");
		assertThat(sc.lookup("java:global/aaa")).isEqualTo("princess");

		sc.bind("java:global/aaa/bbb/ccc", "alloQuoi");
		assertThat(sc.lookup("java:global/aaa/bbb/ccc")).isEqualTo("alloQuoi");
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

	@Test
	public void testMultipleSameSubContexts() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		SimpleInitialContext c1 = (SimpleInitialContext)sc.createSubcontext("java:global/allo");
		SimpleInitialContext c2 = (SimpleInitialContext)sc.createSubcontext("java:global/allo");
		SimpleInitialContext c3 = (SimpleInitialContext)sc.createSubcontext("java:global/allo");
		assertThat(c1).isNotNull();
		assertThat(c2).isNotNull();
		assertThat(c3).isNotNull();
		assertThat(c1.getCurrentEntry()).isEqualTo(c2.getCurrentEntry());
		assertThat(c1.getCurrentEntry()).isEqualTo(c3.getCurrentEntry());
		assertThat(c1.getCurrentSubContext()).isEqualTo(c2.getCurrentSubContext());
		assertThat(c1.getCurrentSubContext()).isEqualTo(c3.getCurrentSubContext());

		c1.createSubcontext("maman");
		c1.createSubcontext("maman");
	}

	private void validateSubContext(final SimpleInitialContext subContext, final JNDINamespace namespace, final String pathExpected)
	{
		assertThat(subContext).isNotNull();
		assertThat(subContext.getCurrentEntry()).isEqualTo(namespace);
		assertThat(subContext.getCurrentSubContext()).isEqualTo(pathExpected);
	}
}