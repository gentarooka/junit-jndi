package junitjndi.contexts;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;

public class SimpleInitialContextForJBossTest
{
	@Before
	public void before()
	{
		SimpleInitialContext.reset();
		System.setProperty(SimpleInitialContext.JBOSS_SPECIFIC_KEY, "true");
	}

	@Test()
	public void testValueForJBossSpecificMode() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		assertThat(sc.isJBossSpecificMode()).isEqualTo(true);
	}

	@Test()
	public void testBind() throws Exception
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
		mapDataSets.put("java:global/tata/titi", "asterix");
		mapDataSets.put("java:global/tata/titi2", "obelix");
		mapDataSets.put("java:global/tata/titi3", "idefix");
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
	public void testLookupContext() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		sc.createSubcontext("abc");
		sc.lookup("abc");
	}

	@Test()
	public void testMultipleSameSubContexts() throws Exception
	{
		final SimpleInitialContext sc = new SimpleInitialContext();
		sc.createSubcontext("java:global/allo");
		sc.createSubcontext("java:global/allo");
	}
}
