package com.camunda.consulting.webserviceExample;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.init;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;

import java.io.File;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.camunda.consulting.webservice.offertantrag.Offertantrag;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(Arquillian.class)
public class ArquillianTest {

  private static final String PROCESS_DEFINITION_KEY = "webservice-example";

  @Deployment
  public static WebArchive createDeployment() {
    // resolve given dependencies from Maven POM
    File[] libs = Maven.resolver()
      .offline(false)
      .loadPomFromFile("pom.xml")
      .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();

    return ShrinkWrap
            .create(WebArchive.class, "webservice-example.war")
            // add needed dependencies
            .addAsLibraries(libs)
            // prepare as process application archive for camunda BPM Platform
            .addAsResource("META-INF/processes.xml", "META-INF/processes.xml")
            // enable CDI
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            // boot JPA persistence unit
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            // add your own classes (could be done one by one as well)
            .addPackages(true, "com.camunda.consulting") // not recursive to skip package 'nonarquillian'
            // add process definition
            .addAsResource("Offerterstellung.bpmn")
            .addAsManifestResource("META-INF/MANIFEST.MF", "MANIFEST.MF")
    ;
  }

  @Inject
  private ProcessEngine processEngine;

  @Before
  public void setup() {
	init(processEngine);
  }

  /**
   * Tests that the process is executable and reaches its end.
   */
  @Test

  public void testProcessExecution() throws Exception {
    ProcessInstance pi = runtimeService().startProcessInstanceByKey("offerterstellung");

    assertThat(pi).isWaitingAt("offertantrag_erfassen");

//    Offertantrag offertantrag = new Offertantrag(VersicherungsdeckungEnum.Einzelperson, new Date(), VersicherungsdauerEnum.Kurzfristversicherung31Tage, new Basispaket());
//    Task offertantragErfassen = taskQuery().processInstanceId(pi.getId()).singleResult();
//    complete(offertantragErfassen, withVariables("offertantrag", offertantrag));

//    ObjectValue variableTyped = runtimeService().getVariableTyped(pi.getProcessInstanceId(), "offertantrag", false);
//    System.out.println("Offerte als XML? " + variableTyped.getValueSerialized());
//    variableTyped = runtimeService().getVariableTyped(pi.getProcessInstanceId(), "offertantrag", true);
//    Offertantrag offertantragAusgelesen = (Offertantrag) variableTyped.getValue();
//    assertThat(offertantragAusgelesen.getEnthaltenesPaket()).isInstanceOf(Basispaket.class);

    String string = "{\"enthaltenesPaket\":{\"type\":\"basispaket\"},\"versicherungsbeginn\":\"2015-06-10T22:00:00.000Z\"}";
    ObjectValue foo = Variables.serializedObjectValue(string)
      .serializationDataFormat("application/json")
      .objectTypeName(Offertantrag.class.getName())
      .create();
    runtimeService().setVariable(pi.getId(), "foo", foo);
    Offertantrag bar = (Offertantrag) runtimeService().getVariable(pi.getId(), "foo");

  }

}
