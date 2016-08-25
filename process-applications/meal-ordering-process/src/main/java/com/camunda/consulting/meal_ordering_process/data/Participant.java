package com.camunda.consulting.meal_ordering_process.data;

import java.io.Serializable;

public class Participant implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private String name;
  private String email;
  
  public Participant(String name, String email) {
    super();
    this.name = name;
    this.email = email;
  }
  
  public Participant() {
    super();
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

}
