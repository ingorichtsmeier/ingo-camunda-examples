# BPMN Processes with JavaEE

A small testdriven example using JEE entities and session bean from a BPMN process.

## Business logic

Auctions (see [Auction.java](src/main/java/com/camunda/consulting/auction/domain/Auction.java)) can have multiple bids (See [Bid.java](src/main/java/com/camunda/consulting/auction/domain/Bid.java)). An auction service is responsible to maintain the auctions and accept bids for an auction (see [AuctionService.java](/src/main/java/com/camunda/consulting/auction/service/AuctionService.java)).

## Process logic

### BPMN Diagram

Each time an auction is created, a process is started to approve the auction and tweets about it. Then bids can be placed until the auction is ended. The final activity is to ship the item.

### Data access

In the process, a data acessor `AuctionProcessVariables` helps to access all process variables in a typesafe manner.

## Tests

### Test the business logic

Use an Arquillian test to check the basic business logic: add an auction and a bid to this auction and test the auction-id of the inserted bid. (see [ArquillianTest.java](/src/test/java/com/camunda/consulting/auction/arquillian/ArquillianTest.java))

### Test the process logic

Use a JUnit test to through the process step by step (see [ProcessTest.java](/src/test/java/com/camunda/consulting/auction/ProcessTest.java)).

1. It registers the CreateTweetDelegate with a mocked tweetService.
2. Test the happy path. Create an Auction and pass it to the process instance.
3. Verify that the content from the auction is passed to the twitter service.
