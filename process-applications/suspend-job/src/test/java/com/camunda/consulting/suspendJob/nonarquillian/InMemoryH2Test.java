package com.camunda.consulting.suspendJob.nonarquillian;

import java.util.List;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.consulting.process_test_coverage.ProcessTestCoverage;
import org.camunda.bpm.engine.impl.jobexecutor.MessageJobDeclaration;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.management.JobDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.camunda.consulting.suspendJob.LoggerDelegate;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "suspend-job";

  // enable more detailed logging
  static {
    LogUtil.readJavaUtilLoggingConfigFromClasspath(); // process engine
    LogFactory.useJdkLogging(); // MyBatis
  }
  
  @Before
  public void setup() {
    init(rule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = { "process.bpmn" })
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }
  
  @Test
  @Deployment(resources = "process.bpmn") 
  public void testSuspendJobOfCertainVersion() {
    // deploy a second version of the process definition
    repositoryService().createDeployment().addClasspathResource("process-more-tasks.bpmn").deploy();
    assertThat(repositoryService().createProcessDefinitionQuery().processDefinitionKey(PROCESS_DEFINITION_KEY).count()).isEqualTo(2);
    
    // query for job definitions of a certain task 
    List<JobDefinition> jobDefinitions = managementService()
        .createJobDefinitionQuery()
        .processDefinitionKey(PROCESS_DEFINITION_KEY)
        .activityIdIn("ServiceTask_5")
        .jobConfiguration(MessageJobDeclaration.ASYNC_AFTER)
        .list();
    assertThat(jobDefinitions).hasSize(2);
    
    // query for job definitions of a certain task of the latest process version
    ProcessDefinition procDef = repositoryService()
        .createProcessDefinitionQuery()
        .processDefinitionKey(PROCESS_DEFINITION_KEY)
        .latestVersion()
        .singleResult();
    jobDefinitions = managementService()
        .createJobDefinitionQuery()
        .processDefinitionId(procDef.getId())
        .activityIdIn("ServiceTask_5")
        .jobConfiguration(MessageJobDeclaration.ASYNC_BEFORE)
        .list();
    assertThat(jobDefinitions).hasSize(1);
    
    // suspend the job definition
    String suspendedJobDefinitionOfAsyncBeforeServiceTask5 = jobDefinitions.get(0).getId();
    managementService().suspendJobDefinitionById(suspendedJobDefinitionOfAsyncBeforeServiceTask5);
    
    // run through all active jobs until the suspended is reached
    Mocks.register("logger", new LoggerDelegate());
    
    ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, withVariables("task", "task4"));
    assertThat(pi).isActive();
    while (jobQuery().active().singleResult() != null) {
      System.out.println(job().getJobDefinitionId());
      execute(job());
    }
    
    // suspended job definition reached
    assertThat(pi).isWaitingAt("ServiceTask_5");
    
    // activate job definition with existing jobs again
    // In cockpit, this is a checkbox 'Include existing jobs?'
    managementService().activateJobDefinitionById(suspendedJobDefinitionOfAsyncBeforeServiceTask5, true);
    
    // execute async-before Service Task 5 Job
    execute(job());
    
    // now waiting at async-after Service Task 5 Job
    assertThat(pi).isWaitingAt("ServiceTask_5");
    
    // execute async-after
    execute(job());
    
    assertThat(pi).isWaitingAt("ServiceTask_6");
    
    ProcessTestCoverage.calculate(pi, processEngine());
  }

}
