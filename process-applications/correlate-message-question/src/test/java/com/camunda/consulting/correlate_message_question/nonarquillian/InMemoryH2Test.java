package com.camunda.consulting.correlate_message_question.nonarquillian;

import org.apache.ibatis.logging.LogFactory;
import org.assertj.core.api.Fail;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.camunda.consulting.correlate_message_question.LoggerDelegate;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @ClassRule
  @Rule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(rule.getProcessEngine());
    Mocks.register("logger", new LoggerDelegate());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = {"receipt-handling.bpmn", "receipt-check.dmn"})
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = {"receipt-handling.bpmn", "receipt-check.dmn"})
  public void testHappyPath() {
	  runtimeService().correlateMessage("Message_receipt", "Test BK", withVariables("receiptValue", 6));
	  
	  // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
	  ProcessInstance processInstance = runtimeService().createProcessInstanceQuery().processInstanceBusinessKey("Test BK").singleResult();
	  assertThat(processInstance).isWaitingAt("checkForFurtherReceiptsTask");
	  execute(job());
	  assertThat(processInstance).isWaitingAt("waitForReceiptTask");
	  execute(job());
	  assertThat(processInstance).hasPassed("warningSentEndEvent");
	  runtimeService().correlateMessage("Message_receipt", "Test BK", withVariables("receiptValue", 4));
	  assertThat(processInstance).isWaitingAt("checkForFurtherReceiptsTask");
	  
	  assertThat(runtimeService().createProcessInstanceQuery().list()).hasSize(1);
	  
	  execute(job());
	  assertThat(processInstance).isEnded();
  }
  
  @Test
  @Deployment(resources = {"receipt-handling.bpmn", "receipt-check.dmn"})
  public void testTwoMessagesSameBusinessKeyAndNotReceiving() {
    runtimeService().correlateMessage("Message_receipt", "Test BK", withVariables("receiptValue", 6));

    ProcessInstance processInstance1 = runtimeService().createProcessInstanceQuery().processInstanceBusinessKey("Test BK").singleResult();
    assertThat(processInstance1).isWaitingAt("checkForFurtherReceiptsTask");
    
    runtimeService().correlateMessage("Message_receipt", "Test BK", withVariables("receiptValue", 7));
    List<ProcessInstance> processInstances = runtimeService().createProcessInstanceQuery().processInstanceBusinessKey("Test BK").list();
    
    assertThat(processInstances).hasSize(2);
    
    ProcessInstance processInstance2 = runtimeService().createProcessInstanceQuery().variableValueEquals("receiptValue", 7).singleResult();
    
    List<Job> jobList = managementService().createJobQuery().list();
    for (Job job : jobList) {
      execute(job);
    }
    
    assertThat(processInstance1).isWaitingAt("waitForReceiptTask");
    assertThat(processInstance2).isWaitingAt("waitForReceiptTask");
    
    try {
      runtimeService().correlateMessage("Message_receipt", "Test BK", withVariables("receiptValue", 4));
      Fail.fail("should get a mismatching message correlation exception");
    } catch (MismatchingMessageCorrelationException e) {
      assertThat(e.getMessage()).contains("2 executions match");
    }
    
  }

}
