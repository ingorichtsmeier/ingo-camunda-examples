package com.camunda.consulting.multiinstanceCallActivity.nonarquillian;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.spring.impl.test.SpringProcessEngineTestCase;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("SpringProcessTest-context.xml")
public class SpringProcessTest extends SpringProcessEngineTestCase {
  
  static {
    LogUtil.readJavaUtilLoggingConfigFromClasspath();
    LogFactory.useJdkLogging();
  }
  
  @Test
  @Deployment(resources = {"process.bpmn", "calledProcess.bpmn"}) 
  public void testStartProcessWithMultiinstanceAndCheckHistory() {
    List<String> inputList = Arrays.asList("Buenos Aires", "Córdoba", "La Plata");
    ProcessInstance pi = runtimeService().startProcessInstanceByKey("multiinstance-call-activity", withVariables("inputList", inputList));
    
    assertThat(pi).isEnded();
    
    List<HistoricVariableInstance> outputListVars = historyService
        .createHistoricVariableInstanceQuery()
        .variableName("outputList")
        .list();
    
    for (HistoricVariableInstance outputList : outputListVars) {
      System.out.println(outputList.getProcessInstanceId() + " " + outputList.getValue());
    }
    
    HistoricVariableInstance outputListVar = historyService
        .createHistoricVariableInstanceQuery()
        .variableName("outputList")
        .processInstanceId(pi.getProcessInstanceId())
        .singleResult();
    
    
    List<String> outputList = (List<String>) outputListVar.getValue();
    
    assertThat(outputList).containsExactly("Buenos Aires", "Córdoba", "La Plata");
  }

  @Test
  @Deployment(resources = {"process.bpmn", "calledProcess.bpmn", "superProcess.bpmn"}) 
  public void testStartSuperProcessAndCheckHistoricVariable() {
    List<String> inputList = Arrays.asList("Buenos Aires", "Córdoba", "La Plata");
    ProcessInstance pi = runtimeService().startProcessInstanceByKey("variable-saver", withVariables("inputList", inputList));
    
    assertThat(pi).isEnded();
    
    HistoricVariableInstance outputListVar = historyService
        .createHistoricVariableInstanceQuery()
        .variableName("outputList")
        .processInstanceId(pi.getProcessInstanceId())
        .singleResult();
    
    
    List<String> outputList = (List<String>) outputListVar.getValue();
    
    assertThat(outputList).containsExactly("Buenos Aires", "Córdoba", "La Plata");
  }
}
