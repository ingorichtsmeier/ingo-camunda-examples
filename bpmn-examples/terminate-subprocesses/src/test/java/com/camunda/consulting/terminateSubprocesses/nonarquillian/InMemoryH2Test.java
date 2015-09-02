package com.camunda.consulting.terminateSubprocesses.nonarquillian;

import java.util.List;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.consulting.process_test_coverage.ProcessTestCoverage;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineTestCase;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.camunda.consulting.terminateSubprocesses.LoggerDelegate;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "contract_handling";

  // enable more detailed logging
  static {
//    LogUtil.readJavaUtilLoggingConfigFromClasspath(); // process engine
//    LogFactory.useJdkLogging(); // MyBatis
  }
  
  @Before
  public void setup() {
    init(rule.getProcessEngine());
    Mocks.register("logger", new LoggerDelegate());
  }
  
  @After
  public void teardown() {
    ProcessTestCoverage.calculate(rule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = {"contract_handling.bpmn", "document_handling.bpmn", "payment_handling.bpmn"})
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }
  
  @Test
  @Deployment(resources = {"contract_handling.bpmn", "document_handling.bpmn", "payment_handling.bpmn"})
  public void testGetPaymentAndDocuments() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    assertThat(processInstance).isWaitingAt("Document_processing", "Payment_processing");
    runtimeService().createMessageCorrelation("documents_received").correlate();
    assertThat(processInstance).hasPassed("Document_processed");
    runtimeService().createMessageCorrelation("payment_received").correlate();
    assertThat(processInstance).hasPassed("Payment_processed").isEnded();
  }
  
  @Test
  @Deployment(resources = {"contract_handling.bpmn", "document_handling.bpmn", "payment_handling.bpmn"})
  public void testCancelPayment() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    runtimeService().createMessageCorrelation("cancellation_payment_received").correlate();
    assertThat(processInstance).hasPassed("Do_cleanup", "contract_cleaned").isEnded();
    List<ProcessInstance> processInstances = processInstanceQuery().list();
    assertThat(processInstances).isEmpty();
  }
  
  @Test
  @Deployment(resources = {"contract_handling.bpmn", "document_handling.bpmn", "payment_handling.bpmn"})
  public void testDecideCancellationDocument() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    allTimersExpired();
    Task document_decide_about_deadline = taskQuery().taskDefinitionKey("decide_about_deadline").processDefinitionKey("document_handling").singleResult();
    complete(document_decide_about_deadline, withVariables("deadline_documents_final", true));
    assertThat(processInstance).isEnded().hasPassed("Do_cleanup", "contract_cleaned");
  }
  
  @Test
  @Deployment(resources = {"contract_handling.bpmn", "document_handling.bpmn", "payment_handling.bpmn"})
  public void testLateCancellation() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    allTimersExpired();
    ProcessInstance documentProcessInstance = processInstanceQuery().processDefinitionKey("document_handling").singleResult();
    assertThat(documentProcessInstance).hasPassed("Deadline_20_days").isWaitingAt("decide_about_deadline");
    runtimeService().createMessageCorrelation("cancellation_documents_received").correlate();
    assertThat(processInstance).hasPassed("Do_cleanup", "contract_cleaned").isEnded();
  }

  private void allTimersExpired() {
    List<Job> timerJobs = jobQuery().list();
    assertThat(timerJobs).hasSize(2);
    for (Job timerJob : timerJobs) {
      execute(timerJob);
    }
  }

}
