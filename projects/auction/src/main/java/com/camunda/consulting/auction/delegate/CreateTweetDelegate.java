package com.camunda.consulting.auction.delegate;

import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.consulting.auction.AuctionProcessVariables;
import com.camunda.consulting.auction.service.TweetService;

@Named
public class CreateTweetDelegate implements JavaDelegate {
  
  private TweetService tweetService;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTweetDelegate.class);
  
  @Inject
  public CreateTweetDelegate(TweetService aTweetService) {
    LOGGER.info("using tweet service {}", aTweetService.getClass().getName());
    this.tweetService = aTweetService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    AuctionProcessVariables auctionProcessVariables = new AuctionProcessVariables(execution);
    String content = auctionProcessVariables.getContent();
    long tweetId = tweetService.publish(content);
    auctionProcessVariables.setTweetId(tweetId);
  }
}
