package com.camunda.consulting.multiinstanceCallActivity.nonarquillian;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NestedPackagesTest {

  @Test
  public void test5NestedLists() throws InterruptedException {
    List<List<String>> myPackage = new ArrayList<List<String>>();
    
    for (int i = 0; i < 5; i++) {
      List<String> myList = new ArrayList<String>();
      int innerBoundary = 6;
      for (int j = 0; j < innerBoundary; j++) {
        myList.add("" + (i * innerBoundary + j));
      }
      myPackage.add(myList);
    }
    
    System.out.println("" + myPackage);
    
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("PackageHandlingProcess", Map.of("package", myPackage));
    
    Thread.sleep(5000);
    
    assertThat(processInstance).isEnded();
  }
}
