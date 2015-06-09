package com.camunda.consulting.webservice.offertantrag;

import spinjar.com.fasterxml.jackson.annotation.JsonSubTypes;
import spinjar.com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import spinjar.com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(  
    use = JsonTypeInfo.Id.NAME,  
    include = JsonTypeInfo.As.PROPERTY,  
    property = "type")  
@JsonSubTypes({  
  @Type(value = Basispaket.class, name = "basispaket"),  
  @Type(value = Sorglospaket.class, name = "sorglospaket") })  
public abstract class Paket {
  
  String type;
  public Paket() {
    super();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
