package junitjndi.contexts;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

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
		assertThat(subContext1).isNotNull();
		assertThat(subContext1.getCurrentEntry()).isEqualTo(JNDINamespace.GLOBAL);
		assertThat(subContext1.getCurrentSubContext()).isEqualTo("aaa");

		final SimpleInitialContext subContext2 = (SimpleInitialContext)subContext1.createSubcontext("bbb");
		assertThat(subContext2).isNotNull();
		assertThat(subContext2.getCurrentEntry()).isEqualTo(JNDINamespace.GLOBAL);
		assertThat(subContext2.getCurrentSubContext()).isEqualTo("aaa/bbb");

		final SimpleInitialContext subContext3 = (SimpleInitialContext)subContext2.createSubcontext("java:global/ccc");
		assertThat(subContext3).isNotNull();
		assertThat(subContext3.getCurrentEntry()).isEqualTo(JNDINamespace.GLOBAL);
		assertThat(subContext3.getCurrentSubContext()).isEqualTo("aaa/bbb/ccc");

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
		assertThat(subContext1).isNotNull();
		assertThat(subContext1.getCurrentEntry()).isEqualTo(JNDINamespace.APPLICATION);
		assertThat(subContext1.getCurrentSubContext()).isEqualTo("niv1");

		final SimpleInitialContext subContext2 = (SimpleInitialContext)subContext1.createSubcontext("niv2");
		assertThat(subContext2).isNotNull();
		assertThat(subContext2.getCurrentEntry()).isEqualTo(JNDINamespace.APPLICATION);
		assertThat(subContext2.getCurrentSubContext()).isEqualTo("niv1/niv2");

		final SimpleInitialContext subContext3 = (SimpleInitialContext)subContext2.createSubcontext("niv3");
		assertThat(subContext3).isNotNull();
		assertThat(subContext3.getCurrentEntry()).isEqualTo(JNDINamespace.APPLICATION);
		assertThat(subContext3.getCurrentSubContext()).isEqualTo("niv1/niv2/niv3");

		final SimpleInitialContext subContext4 = (SimpleInitialContext)subContext3.createSubcontext("niv4");
		assertThat(subContext4).isNotNull();
		assertThat(subContext4.getCurrentEntry()).isEqualTo(JNDINamespace.APPLICATION);
		assertThat(subContext4.getCurrentSubContext()).isEqualTo("niv1/niv2/niv3/niv4");

		sc.bind("java:app/niv1/niv2/niv3/niv4/monExemple", "exemple");
		assertThat(sc.lookup("java:app/niv1/niv2/niv3/niv4/monExemple")).isEqualTo("exemple");
	}
}