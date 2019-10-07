package com.camunda.consulting.selfhealing_generic_process;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ProcessUnitTest {

  @ClassRule
  @Rule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  private static final String PROCESS_DEFINITION_KEY = "SuperProcess";

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(rule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = {"superProcess.bpmn", "genericSubProcess.bpmn"})
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = {"superProcess.bpmn", "genericSubProcess.bpmn"})
  public void testHappyPath() {
    Order order = new Order("15", "Ingo Richtsmeier");
	  ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
	      withVariables("order", order));
	  
	  // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
	  assertThat(processInstance).isWaitingAt("N1ServiceInvocationCallActivity");
	  
	  ProcessInstance subProcessInstance = processInstanceQuery().processDefinitionKey("GenericSubProcess").singleResult();
	  assertThat(subProcessInstance).isWaitingAt("DoTheRealServiceInvocationTask").hasNoVariables();
	  execute(job());
	  
	  assertThat(processInstance).isWaitingAt("N2ServiceInvocationCallActivity").variables().containsEntry("order", new Order("15", "Ingo Richtsmeier", "My first addition", null));
  }

}
