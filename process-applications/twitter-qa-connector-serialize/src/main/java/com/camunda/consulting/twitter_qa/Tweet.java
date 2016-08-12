package com.camunda.consulting.twitter_qa;

public class Tweet {
  
  private String content;
  private Boolean approved;
  private String email;
  private String rejectionComment;
  private Boolean addCounter = false;

  public void reject(String comment) {
    this.rejectionComment = comment;
    this.approved = false;    
  }
  
  public String getRejectionComment() {
    return rejectionComment;
  }

  public String getContent() {
    return content;
  }
  public void setContent(String content) {
    this.content = content;
  }
  public Boolean getApproved() {
    return approved;
  }
  public void setApproved(Boolean approved) {
    this.approved = approved;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public Boolean getAddCounter() {
    return addCounter;
  }
  public void setAddCounter(Boolean addCounter) {
    this.addCounter = addCounter;
  }
}
