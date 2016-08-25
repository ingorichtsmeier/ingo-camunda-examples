package com.camunda.consulting.meal_ordering_process.data;

import java.io.Serializable;

public class MealSelection implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private String participantName;
  private String meal;
  
  public MealSelection() {
    super();
  }
  public MealSelection(String participantName, String meal) {
    super();
    this.participantName = participantName;
    this.meal = meal;
  }
  public String getParticipantName() {
    return participantName;
  }
  public void setParticipantName(String participantName) {
    this.participantName = participantName;
  }
  public String getMeal() {
    return meal;
  }
  public void setMeal(String meal) {
    this.meal = meal;
  }
  
  @Override
  public String toString() {
    return "MealSelection [participantName=" + participantName + ", meal=" + meal + "]";
  }
  
}
