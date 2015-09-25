package com.camunda.consulting.xstreamVariableSerializerPlugin;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "xstream-variable-serializer-plugin";

  // enable more detailed logging
  static {
    LogUtil.readJavaUtilLoggingConfigFromClasspath(); // process engine
    LogFactory.useJdkLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(rule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = "process.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }
  
  @Test
  @Deployment(resources = "process.bpmn")
  public void testSerializeVariable() {
    ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    Customer customer = new Customer();
    customer.setName("testCustomer");
    customer.setId("15");
    runtimeService().setVariable(pi.getId(), "customer", Variables.objectValue(customer).serializationDataFormat("xstream/xml").create());
    Customer variable = (Customer) runtimeService().getVariable(pi.getId(), "customer");
    System.out.println("" + variable);
    assert(variable.getName()).equals("testCustomer");
    ObjectValue variableTyped = runtimeService().getVariableTyped(pi.getId(), "customer", false);
    System.out.println("" + variableTyped.getValueSerialized());
    assert(variableTyped.getValueSerialized()).contains(".Customer>");
  }

}
