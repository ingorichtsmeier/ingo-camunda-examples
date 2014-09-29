package com.camunda.consulting.antiAgileTwitter;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "anti-agile-twitter";

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
  @Deployment(resources = "anti-agile-twitter.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }
  
  @Test
  @Deployment(resources = "anti-agile-twitter.bpmn")
  public void runCompleteProcess() {
    ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    Task writeTweet = taskQuery().singleResult();
    assertThat(writeTweet).hasCandidateGroup("marketing");
    complete(writeTweet);
    Task reviewTweet = taskQuery().singleResult();
    complete(reviewTweet, withVariables("approved", "maybe"));
    Task rewriteTweet = taskQuery().singleResult();
    assertThat(rewriteTweet).hasCandidateGroup("marketing");
    complete(rewriteTweet);
    reviewTweet = taskQuery().singleResult();
    complete(reviewTweet, withVariables("approved", "no"));
    assertThat(pi).hasPassed("UserTask_1", "UserTask_2", "EndEvent_1")
      .isEnded();
  }

}
