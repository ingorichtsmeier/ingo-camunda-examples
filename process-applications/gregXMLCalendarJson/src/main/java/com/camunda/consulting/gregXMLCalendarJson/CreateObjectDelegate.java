package com.camunda.consulting.gregXMLCalendarJson;

import java.util.GregorianCalendar;

import javax.inject.Named;
import javax.xml.datatype.DatatypeFactory;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import com.camunda.consulting.gregXMLCalendarJson.data.ExampleData;

@Named
public class CreateObjectDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    GregorianCalendar now = new GregorianCalendar();
    execution.setVariable("exampleData", new ExampleData("hallo", DatatypeFactory.newInstance().newXMLGregorianCalendar(now)));
  }

}
