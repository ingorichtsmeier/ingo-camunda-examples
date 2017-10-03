package com.camunda.consulting.auction;

import org.camunda.bpm.engine.delegate.VariableScope;

import com.camunda.consulting.auction.domain.Auction;

public class AuctionProcessVariables {
  
  public static final String TWEET_ID = "tweetId";
  public static final String AUCTION = "auction";
  
  private VariableScope variableScope;
  
  public AuctionProcessVariables(VariableScope scope) {
    this.variableScope = scope;
  }
  
  public String getContent() {
    return ((Auction) variableScope.getVariable(AUCTION)).getTitle();
  }

  public void setTweetId(long id) {
    variableScope.setVariable(TWEET_ID, id);
  }
}
