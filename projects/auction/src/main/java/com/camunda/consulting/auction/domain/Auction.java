package com.camunda.consulting.auction.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Auction implements Serializable {

  private static final long serialVersionUID = -1005627217448939935L;
  @Id
  @GeneratedValue
  private Long id;
  
  private String title;
  private String description;
  private Date endDate;
  private boolean authorized = false;
  private Long tweetId;
  @OneToMany(mappedBy="auction")
  private List<Bid> bids = new ArrayList<Bid>();
  @OneToOne
  private Bid highestBid;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
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
  public Long getTweetId() {
    return tweetId;
  }
  public void setTweetId(Long tweetId) {
    this.tweetId = tweetId;
  }
  public List<Bid> getBids() {
    return bids;
  }
  public void setBids(List<Bid> bids) {
    this.bids = bids;
  }
  public Bid getHighestBid() {
    return highestBid;
  }
  public void setHighestBid(Bid highestBid) {
    this.highestBid = highestBid;
  }
  public String getEndDateIso() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(endDate);
  }
}
