package com.camunda.consulting.gregXMLCalendarJson.data;

import javax.xml.datatype.XMLGregorianCalendar;

public class ExampleData {
  
  private String name;
  private XMLGregorianCalendar date;
  
  public ExampleData() {
    super();
  }
  
  public ExampleData(String name, XMLGregorianCalendar date) {
    super();
    this.name = name;
    this.date = date;
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public XMLGregorianCalendar getDate() {
    return date;
  }
  public void setDate(XMLGregorianCalendar date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return "ExampleData [name=" + name + ", date=" + date.toXMLFormat() + "]";
  }
  
  

}
