package com.camunda.consulting.javascriptValidation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;

public class CreateCustomerDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    Customer customer = new Customer("example-name", "example-street", "example-city");
    ObjectValue customerVariable = Variables.objectValue(customer).serializationDataFormat("application/json").create();
    execution.setVariable("customer", customerVariable);
  }

}
