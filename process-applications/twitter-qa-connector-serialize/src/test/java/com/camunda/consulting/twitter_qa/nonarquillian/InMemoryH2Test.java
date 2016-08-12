package com.camunda.consulting.twitter_qa.nonarquillian;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.spin.DataFormats;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.camunda.consulting.twitter_qa.CreateTweetDelegate;
import com.camunda.consulting.twitter_qa.LoggerDelegate;
import com.camunda.consulting.twitter_qa.Tweet;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.consulting.process_test_coverage.ProcessTestCoverage;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "twitter-qa";

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
  @Deployment(resources = "process.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = {"process.bpmn", "approval.dmn"})
  public void testHappyPath() {
    Mocks.register("createTweetDelegate", new LoggerDelegate());
    Tweet tweet = new Tweet();
    tweet.setContent("Tweet aus JUnit mit komplexem Objekt");
    tweet.setEmail("jakob.freund@camunda.com");
    tweet.setAddCounter(true);
    TypedValue typedTweetValue = Variables.objectValue(tweet).serializationDataFormat(DataFormats.JSON_DATAFORMAT_NAME).create();
    
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("tweet", typedTweetValue); 
    
	  ProcessInstance processInstance = 
	      runtimeService()
	      .startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
	  
	  // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
	  assertThat(processInstance).isWaitingAt("nachricht_pruefen");
	  
	  ObjectValue tweetRead = runtimeService().getVariableTyped(processInstance.getId(), "tweet", false);
	  assertThat(tweetRead.getSerializationDataFormat()).isEqualTo("application/json");
	  
	  assertThat(task()).hasCandidateGroup("management").hasName("Nachricht prüfen");
	  
	  tweet.setApproved(true);
	  complete(task(), withVariables("tweet", tweet));
	  
	  assertThat(processInstance).isWaitingAt("tweet_veroeffentlichen");
	  execute(job());
	  
	  assertThat(processInstance).isEnded().hasPassed("tweet_veroeffentlichen");
	  
	  // To generate the coverage report for a single tests add this line as the last line of your test method:
	  ProcessTestCoverage.calculate(processInstance, rule.getProcessEngine());
  }
  
  @Test
  @Deployment(resources = "process.bpmn")
  public void testTweetRejected() {
    Tweet tweet = new Tweet();
    tweet.setContent("ablehnen");
    tweet.setEmail("jakob.freund@camunda.com");
    tweet.setApproved(false);

    ProcessInstance processInstance = runtimeService()
        .createProcessInstanceByKey(PROCESS_DEFINITION_KEY)
        .startAfterActivity("nachricht_pruefen")
        .setVariables(
            withVariables("tweet", tweet))
        .execute();
    assertThat(processInstance).isEnded().hasPassed("end_nachricht_geloescht");
    
    ProcessTestCoverage.calculate(processInstance, processEngine());
  }
  
  @Test
  @Deployment(resources = "approval.dmn")
  public void testRejectCamundaNotSuper() {
    Map<String, Object> variables = withVariables("content", "Camunda ist nicht super",
        "email", "egal");
    DmnDecisionTableResult decisionTableResult = processEngine()
        .getDecisionService()
        .evaluateDecisionTableByKey("tweet_approval", variables);
    
    assertThat(decisionTableResult.getSingleResult())
        .containsEntry("approved", false);
  }
  
  @Test
  @Deployment(resources = "approval.dmn")
  public void testApproveCamundaSuper() {
    Map<String, Object> variables = withVariables("content", "Camunda ist super",
        "email", "egal");
    DmnDecisionTableResult decisionTableResult = processEngine()
        .getDecisionService()
        .evaluateDecisionTableByKey("tweet_approval", variables);
    
    assertThat(decisionTableResult.getSingleResult())
        .containsEntry("approved", true);
  }
  
  @Test
  @Deployment(resources = "approval.dmn")
  public void testDenyEverythingElse() {
    Map<String, Object> variables = 
        withVariables("content", "egal", "email", "egal");
    
    DmnDecisionTableResult decisionTableResult = processEngine()
        .getDecisionService()
        .evaluateDecisionTableByKey("tweet_approval", variables);
    
    assertThat(decisionTableResult.getSingleResult())
        .containsEntry("approved", false);
  }

  @After
  public void calculateCoverageForAllTests() throws Exception {
    ProcessTestCoverage.calculate(rule.getProcessEngine());
  }  

}
