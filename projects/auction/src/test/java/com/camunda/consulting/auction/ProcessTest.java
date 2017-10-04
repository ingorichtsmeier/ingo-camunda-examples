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
import org.powermock.modules.junit4.PowerMockRunner;

import com.camunda.consulting.auction.delegate.CreateTweetDelegate;
import com.camunda.consulting.auction.domain.Auction;
import com.camunda.consulting.auction.service.TweetService;

@RunWith(PowerMockRunner.class)
public class ProcessTest {
  
  @Rule
  public ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();
  
  TweetService tweetService = mock(TweetService.class);
  
  @Before
  public void setup() {
    Mocks.register("createTweetDelegate", new CreateTweetDelegate(tweetService));
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
    // Mock the TweetService
    when(tweetService.publish("test auction object")).thenReturn(15L);
    
    Auction auction = new Auction();
    auction.setTitle("test auction object");
    auction.setDescription("some test description");
    
    ProcessInstance processInstance = runtimeService()
        .startProcessInstanceByKey("AuctionProcess", withVariables(
            AuctionProcessVariables.AUCTION, auction));
    
    assertThat(processInstance).isWaitingAt("AuthorizeAuction_Task");
    
    auction.setAuthorized(true);
    auction.setEndDate(new Date());
    complete(task(), withVariables(AuctionProcessVariables.AUCTION, auction));
    
    assertThat(processInstance).isWaitingAt("AuctionEnded_TimerEvent")
      .variables().containsEntry(AuctionProcessVariables.TWEET_ID, 15L);
    
    verify(tweetService).publish("test auction object");
    
    auction.setNumberOfBids(13L);
    runtimeService().setVariable(processInstance.getId(), AuctionProcessVariables.AUCTION, auction);
    
    execute(job());
    
    assertThat(processInstance).isWaitingAt("PrepareBilling_Task");
    
    complete(task());
    
    assertThat(processInstance).isEnded().hasPassed("AuctionFinished_EndEvent");
  }
  
  @Deployment(resources = "auctionProcess.bpmn")
  @Test
  public void testNoBids() {
    Auction auction = new Auction();
    auction.setNumberOfBids(0L);
    ProcessInstance processInstance = runtimeService()
        .createProcessInstanceByKey("AuctionProcess")
        .startAfterActivity("AuctionEnded_TimerEvent")
        .setVariable(AuctionProcessVariables.AUCTION, auction)
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
