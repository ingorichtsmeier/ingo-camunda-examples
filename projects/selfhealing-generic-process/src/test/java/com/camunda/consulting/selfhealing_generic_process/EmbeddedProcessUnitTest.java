package com.camunda.consulting.selfhealing_generic_process;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.ProcessEngineException;
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
public class EmbeddedProcessUnitTest {

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
  @Deployment(resources = {"superProcess.bpmn", "genericSubProcessWithEmbeddedSelfHealing.bpmn"})
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = {"superProcess.bpmn", "genericSubProcessWithEmbeddedSelfHealing.bpmn"})
  public void testHappyPath() {
    Order order = new Order("25", "Ingo Richtsmeier Embedded");
	  ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
	      withVariables("order", order));
	  
	  // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
	  assertThat(processInstance).isWaitingAt("N1ServiceInvocationCallActivity");
	  
	  ProcessInstance subProcessInstance = processInstanceQuery().processDefinitionKey("GenericSubProcess").singleResult();
	  assertThat(subProcessInstance).isWaitingAt("DoTheRealServiceInvocationTask").hasNoVariables();
	  runtimeService().setVariable(subProcessInstance.getId(), "shouldComplete", true);
	  execute(job());
	  
	  assertThat(processInstance).isWaitingAt("N2ServiceInvocationCallActivity").variables().containsEntry("order", new Order("25", "Ingo Richtsmeier Embedded", "My first addition", null));
  }

  @Test
  @Deployment(resources = {"superProcess.bpmn", "genericSubProcessWithEmbeddedSelfHealing.bpmn"})
  public void testFailingServiceAndIgnore() {
    Order order = new Order("26", "Failing and Ignore Embedded");
    ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
        withVariables("order", order));
    
    // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
    assertThat(processInstance).isWaitingAt("N1ServiceInvocationCallActivity");
    
    ProcessInstance subProcessInstance = processInstanceQuery().processDefinitionKey("GenericSubProcess").singleResult();
    assertThat(subProcessInstance).isWaitingAt("DoTheRealServiceInvocationTask").hasNoVariables();
    runtimeService().setVariable(subProcessInstance.getId(), "shouldComplete", false);
    execute(job());
    
    assertThat(subProcessInstance).isWaitingAt("InvokeSelfHealingTask");
    
    runtimeService().setVariable(subProcessInstance.getId(), "self_healing", "ignore");
    execute(job());
    assertThat(subProcessInstance).isEnded().hasPassed("GenericSubprocessFinishedEndEvent");
  }
  
  @Test
  @Deployment(resources = {"superProcess.bpmn", "genericSubProcessWithEmbeddedSelfHealing.bpmn"})
  public void testFailingServiceAndRetry() {
    Order order = new Order("27", "Failing and Retry Embedded");
    ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
        withVariables("order", order));
    
    // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
    assertThat(processInstance).isWaitingAt("N1ServiceInvocationCallActivity");
    
    ProcessInstance subProcessInstance = processInstanceQuery().processDefinitionKey("GenericSubProcess").singleResult();
    assertThat(subProcessInstance).isWaitingAt("DoTheRealServiceInvocationTask").hasNoVariables();
    runtimeService().setVariable(subProcessInstance.getId(), "shouldComplete", false);
    execute(job());
    
    assertThat(subProcessInstance).isWaitingAt("InvokeSelfHealingTask");
    
    runtimeService().setVariable(subProcessInstance.getId(), "self_healing", "retry");
    execute(job());
    assertThat(subProcessInstance).isWaitingAt("N10SecondsEvent");
    execute(job());
    assertThat(subProcessInstance).isWaitingAt("DoTheRealServiceInvocationTask");
    runtimeService().setVariable(subProcessInstance.getId(), "shouldComplete", true);
    execute(job());
    assertThat(subProcessInstance).isEnded().hasPassed("GenericSubprocessFinishedEndEvent");
  }
  
  @Test
  @Deployment(resources = {"superProcess.bpmn", "genericSubProcessWithEmbeddedSelfHealing.bpmn"})
  public void testFailingServiceAndErrorNotClarified() {
    Order order = new Order("28", "Failing and Error handling aborting embedded");
    ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
        withVariables("order", order));
    
    // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
    assertThat(processInstance).isWaitingAt("N1ServiceInvocationCallActivity");
    
    ProcessInstance subProcessInstance = processInstanceQuery().processDefinitionKey("GenericSubProcess").singleResult();
    assertThat(subProcessInstance).isWaitingAt("DoTheRealServiceInvocationTask").hasNoVariables();
    runtimeService().setVariable(subProcessInstance.getId(), "shouldComplete", false);
    execute(job());
    
    assertThat(subProcessInstance).isWaitingAt("InvokeSelfHealingTask");
    
    runtimeService().setVariable(subProcessInstance.getId(), "self_healing", "error");
    execute(job());
    assertThat(subProcessInstance).isWaitingAt("HandleErrorTask").externalTask().hasTopicName("errorHandling");
    try {
      complete(externalTask(), withVariables("error_clarified", false));
    } catch (ProcessEngineException e) {
      fail("Uncatched error event");
    }
    assertThat(subProcessInstance).isEnded().hasPassed("ErrorKeptAndProcessAbortedEndEvent1");
    assertThat(processInstance).isEnded().hasNotPassed("N2ServiceInvocationCallActivity");
  }
  
  @Test
  @Deployment(resources = {"superProcess.bpmn", "genericSubProcessWithEmbeddedSelfHealing.bpmn"})
  public void testFailingServiceAndErrorClarified() {
    Order order = new Order("29", "Failing and Successful error handling embedded");
    ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
        withVariables("order", order));
    
    // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
    assertThat(processInstance).isWaitingAt("N1ServiceInvocationCallActivity");
    
    ProcessInstance subProcessInstance = processInstanceQuery().processDefinitionKey("GenericSubProcess").singleResult();
    assertThat(subProcessInstance).isWaitingAt("DoTheRealServiceInvocationTask").hasNoVariables();
    runtimeService().setVariable(subProcessInstance.getId(), "shouldComplete", false);
    execute(job());
    
    assertThat(subProcessInstance).isWaitingAt("InvokeSelfHealingTask");
    
    runtimeService().setVariable(subProcessInstance.getId(), "self_healing", "error");
    execute(job());
    assertThat(subProcessInstance).isWaitingAt("HandleErrorTask").externalTask().hasTopicName("errorHandling");
    complete(externalTask(), withVariables("error_clarified", true));
    assertThat(subProcessInstance).isEnded().hasPassed("GenericSubprocessFinishedEndEvent");
  }
}
