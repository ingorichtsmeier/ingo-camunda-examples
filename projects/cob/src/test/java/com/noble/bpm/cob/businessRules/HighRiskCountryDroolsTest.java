package com.noble.bpm.cob.businessRules;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class HighRiskCountryDroolsTest {
	
	static {
		LogUtil.readJavaUtilLoggingConfigFromClasspath();
	}

	@Test
	public void testHighRiskCounty() {
		DroolsCountry country = new DroolsCountry();
		country.setName("Algeria");
		
		StatefulKnowledgeSession workingMemory = createWorkingMemory();
		workingMemory.insert(country);
		int numberOfRules = workingMemory.fireAllRules();
		assertEquals(1, numberOfRules);
		
		assertTrue("Rules should evaluate Algeria", country.getName().equals("Algeria"));
		assertTrue("Algeria is a high risk country", country.isHighRisk());
		assertEquals("counterparty is in a high risk country", country.getHint());
	}
	
	@Test
	public void testNoHighRisk() {
		DroolsCountry country = new DroolsCountry();
		country.setName("Afghanistan");
		
		StatefulKnowledgeSession workingMemory = createWorkingMemory();
		workingMemory.insert(country);
		int numberOfRules = workingMemory.fireAllRules();
		assertEquals(1, numberOfRules);
		
		assertTrue("Rules should evaluate Afghanistan", country.getName().equals("Afghanistan"));
		assertFalse("Afghanistan is a high risk country", country.isHighRisk());
		assertNull("Hint should be empty", country.getHint());
	}
	
	@Test
	public void testWithReflectionPojo() throws InstantiationException, 
			IllegalAccessException, 
			ClassNotFoundException, 
			NoSuchFieldException {
		Class<?> clazz = Class.forName("com.noble.bpm.cob.businessRules.DroolsCountry");
		Object country = clazz.newInstance();
		Field setName = clazz.getDeclaredField("name");
		setName.setAccessible(true);
		setName.set(country, "Luxembourg");
		
		StatefulKnowledgeSession workingMemory = createWorkingMemory();
		workingMemory.insert(country);
		int numberOfRules = workingMemory.fireAllRules();
		assertEquals(1, numberOfRules);
		
		assertTrue("Rules should evaluate Luxembourg", ((DroolsCountry) country).getName().equals("Luxembourg"));
		assertTrue("Luxembourg is a high risk country", ((DroolsCountry) country).isHighRisk());
		assertEquals("counterparty is in a high risk country", ((DroolsCountry) country).getHint());
	}
	
	private StatefulKnowledgeSession createWorkingMemory() {
		DecisionTableConfiguration dtableconfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
		dtableconfiguration.setInputType( DecisionTableInputType.XLS);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add( 
				ResourceFactory.newClassPathResource("businessRules/HighRiskCountries.xls", getClass()),
				ResourceType.DTABLE,
			    dtableconfiguration );
		return kbuilder.newKnowledgeBase().newStatefulKnowledgeSession();
	}

}
