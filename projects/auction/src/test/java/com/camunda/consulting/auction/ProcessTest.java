package com.camunda.consulting.auction;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.camunda.consulting.auction.delegate.CreateTweetDelegate;
import com.camunda.consulting.auction.domain.Auction;
import com.camunda.consulting.auction.service.TweetService;

@RunWith(PowerMockRunner.class)
public class ProcessTest {
  
  @Rule
  public ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();
  
  @Mock 
  TweetService tweetService; 
  
  @Before
  public void setup() {
    CreateTweetDelegate createTweetDelegate = new CreateTweetDelegate();
    createTweetDelegate.setTweetService(tweetService);
    
    Mocks.register("createTweetDelegate", createTweetDelegate);
  }
  
  @After
  public void tearDown() {
    Mocks.reset();
  }
  
  @Deployment(resources = "auctionProcess.bpmn")
  @Test
  public void testParsingAndDeployment() {
  }
  
  @Deployment(resources = "auctionProcess.bpmn")
  @Test
  public void testHappyPath() throws Exception {
    Auction auction = new Auction();
    auction.setTitle("test auction object");
    auction.setDescription("some test description");
    
    ProcessInstance processInstance = runtimeService()
        .startProcessInstanceByKey("AuctionProcess", withVariables(
            AuctionProcessVariables.AUCTION, auction,
            AuctionProcessVariables.NUMBER_OF_BIDS, 0));
    
    assertThat(processInstance).isWaitingAt("AuthorizeAuction_Task");
    
    auction.setAuthorized(true);
    auction.setEndDate(new Date());
    complete(task(), withVariables(AuctionProcessVariables.AUCTION, auction));
    
    assertThat(processInstance).isWaitingAt("AuctionEnded_TimerEvent").hasVariables(AuctionProcessVariables.TWEET_ID);
    
    verify(tweetService).publish("test auction object");
    
    runtimeService().setVariable(processInstance.getId(), AuctionProcessVariables.NUMBER_OF_BIDS, 13);
    
    execute(job());
    
    assertThat(processInstance).isWaitingAt("PrepareBilling_Task");
    
    complete(task());
    
    assertThat(processInstance).isEnded().hasPassed("AuctionFinished_EndEvent");
  }
  
  @Deployment(resources = "auctionProcess.bpmn")
  @Test
  public void testNoBids() {
    ProcessInstance processInstance = runtimeService()
        .createProcessInstanceByKey("AuctionProcess")
        .startAfterActivity("AuctionEnded_TimerEvent")
        .setVariable(AuctionProcessVariables.NUMBER_OF_BIDS, 0)
        .executeWithVariablesInReturn();
    
    assertThat(processInstance).isEnded().hasPassed("NoBids_EndEvent");
  }
  
  @Deployment(resources = "auctionProcess.bpmn")
  @Test
  public void testNotAuthorized() {
    Auction auction = new Auction();
    auction.setAuthorized(false);
    ProcessInstance processInstance = runtimeService()
        .createProcessInstanceByKey("AuctionProcess")
        .startAfterActivity("AuthorizeAuction_Task")
        .setVariable(AuctionProcessVariables.AUCTION, auction)
        .executeWithVariablesInReturn();
    
    assertThat(processInstance).isEnded().hasPassed("AuctionDeclined_EndEvent");    
  }

}
