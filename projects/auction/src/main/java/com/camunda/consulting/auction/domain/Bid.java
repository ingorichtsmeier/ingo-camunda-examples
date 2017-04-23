package com.camunda.consulting.auction.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Bid {
  
  @Id
  @GeneratedValue
  private Long id;
  @ManyToOne
  private Auction auction;
  private Double amount;
  private String bidderName;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public Auction getAuction() {
    return auction;
  }
  public void setAuction(Auction auction) {
    this.auction = auction;
  }
  public Double getAmount() {
    return amount;
  }
  public void setAmount(Double amount) {
    this.amount = amount;
  }
  public String getBidderName() {
    return bidderName;
  }
  public void setBidderName(String bidderName) {
    this.bidderName = bidderName;
  }

}
