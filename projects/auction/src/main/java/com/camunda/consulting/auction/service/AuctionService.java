package com.camunda.consulting.auction.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.camunda.consulting.auction.domain.Auction;
import com.camunda.consulting.auction.domain.Bid;

@Stateless
public class AuctionService {

  @PersistenceContext
  EntityManager em;
  
  public Auction saveAuction(Auction auction) {
    return this.em.merge(auction);
  }
  
  public Bid saveBid(Bid bid) {
    return this.em.merge(bid);
  }
}
