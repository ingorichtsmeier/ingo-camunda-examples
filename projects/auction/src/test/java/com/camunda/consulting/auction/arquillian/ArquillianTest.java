package com.camunda.consulting.auction.arquillian;

import java.io.File;
import java.util.Date;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.camunda.consulting.auction.domain.Auction;
import com.camunda.consulting.auction.domain.Bid;
import com.camunda.consulting.auction.service.AuctionService;

@RunWith(Arquillian.class)
public class ArquillianTest {
  @Deployment
  public static WebArchive createDeployment() {
    // resolve given dependencies from Maven POM
    File[] libs = Maven.resolver()
      .offline(false)
      .loadPomFromFile("pom.xml")
      .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();

    return ShrinkWrap
            .create(WebArchive.class, "auction-cdi-test.war")
            // add needed dependencies
            .addAsLibraries(libs)
            // enable CDI
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            // boot JPA persistence unit
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            // add your own classes (could be done one by one as well)
//            .addPackages(false, "com.camunda.consulting.auction.domain") // not recursive to skip package 'nonarquillian'
            // now you can add additional stuff required for your test case
            .addClass(Auction.class)
            .addClass(Bid.class)
            .addClass(AuctionService.class)
    ;
  }
  
  @Inject 
  private AuctionService auctionService;
  
  @Test
  public void test() {
    Auction auction = new Auction();
    auction.setTitle("my test auction");
    auction.setDescription("Aweful");
    auction.setEndDate(new Date());
    System.out.println("add action to service");
    auction = auctionService.saveAuction(auction);
    System.out.println("action added");
    
    Bid bid = new Bid();
    bid.setBidderName("Joe");
    bid.setAmount(39.78);
    bid = auctionService.saveBid(bid);
    System.out.println("bid added");
    
    auction.setAuthorized(true);
    auction = auctionService.saveAuction(auction);
    System.out.println("auction " + auction.getId() + " changed");
    
    bid.setAuction(auction);
    auctionService.saveBid(bid);
    System.out.println("bid " + bid.getId() + " added to auction " + auction.getId());
  }

}
