package com.camunda.consulting.multiinstanceCallActivity.nonarquillian;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringProcessTest {
  
  @Test
  @Deployment(resources = {"process.bpmn", "calledProcess.bpmn"}) 
  public void testStartProcessWithMultiinstanceAndCheckHistory() throws InterruptedException {
    List<String> inputList = Arrays.asList("Buenos Aires", "Córdoba", "La Plata", "Brasilia", "Rio de Janero");
    ProcessInstance pi = runtimeService().startProcessInstanceByKey("multiinstance-call-activity", withVariables("inputList", inputList));
    
    Thread.sleep(5000);
    
    assertThat(pi).isEnded();
    
    assertThat(pi).variables()
        .extractingByKey("outputList", as(InstanceOfAssertFactories.LIST))
        .containsAnyOf("Buenos Aires", "Córdoba", "La Plata");
  }

  @Test
  @Deployment(resources = {"process.bpmn", "calledProcess.bpmn", "superProcess.bpmn"}) 
  public void testStartSuperProcessAndCheckHistoricVariable() {
    List<String> inputList = Arrays.asList("Buenos Aires", "Córdoba", "La Plata");
    ProcessInstance pi = runtimeService().startProcessInstanceByKey("variable-saver", withVariables("inputList", inputList));
    
    assertThat(pi).isEnded();
    
    assertThat(pi).variables()
        .extractingByKey("outputList", as(InstanceOfAssertFactories.LIST))
        .containsExactly("Buenos Aires", "Córdoba", "La Plata");
  }
}
