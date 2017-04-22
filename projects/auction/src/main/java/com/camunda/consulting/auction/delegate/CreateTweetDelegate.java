package com.camunda.consulting.auction.delegate;

import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import com.camunda.consulting.auction.AuctionProcessVariables;
import com.camunda.consulting.auction.service.TweetService;

@Named
public class CreateTweetDelegate implements JavaDelegate {
  
  @Inject
  private TweetService tweetService;

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    AuctionProcessVariables auctionProcessVariables = new AuctionProcessVariables(execution);
    String content = auctionProcessVariables.getContent();
    long tweetId = tweetService.publish(content);
    auctionProcessVariables.setTweetId(tweetId);
  }

  /**
   * setter to test in JUnit without CDI
   * @param mockedService
   */
  public void setTweetService(TweetService mockedService) {
    this.tweetService = mockedService;
  }

}
