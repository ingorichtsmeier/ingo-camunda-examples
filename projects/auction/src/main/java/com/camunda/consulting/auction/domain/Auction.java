package com.camunda.consulting.auction.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Auction implements Serializable {

  private static final long serialVersionUID = -1005627217448939935L;
  
  private String title;
  private String description;
  private Date endDate;
  private boolean authorized = false;
  private long tweetId;
  
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public Date getEndDate() {
    return endDate;
  }
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
  public boolean isAuthorized() {
    return authorized;
  }
  public void setAuthorized(boolean authorized) {
    this.authorized = authorized;
  }
  public long getTweetId() {
    return tweetId;
  }
  public void setTweetId(long tweetId) {
    this.tweetId = tweetId;
  }
  
  public String getEndDateIso() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(endDate);
  }
}
