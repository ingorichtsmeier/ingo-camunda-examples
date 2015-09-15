package com.camunda.consulting.javascriptValidation;

public class Customer {
  
  private String name;
  private String street;
  private String place;
  
  public Customer(String name, String street, String place) {
    super();
    this.name = name;
    this.street = street;
    this.place = place;
  }

  public Customer() {
    super();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getPlace() {
    return place;
  }

  public void setPlace(String place) {
    this.place = place;
  }  

}
