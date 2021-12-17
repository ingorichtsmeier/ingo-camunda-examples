package com.camunda.consulting.java8_datatypes;

import java.time.OffsetDateTime;

public class DateObjectDTO {
  
  private OffsetDateTime someDate;
  
  public OffsetDateTime getSomeDate() {
    return this.someDate;
  }
  
  public void setSomeDate(OffsetDateTime offsetDateTime) {
    this.someDate = offsetDateTime;
  }

  @Override
  public String toString() {
    return "DateObjectDTO [someDate=" + someDate + "]";
  }

}
