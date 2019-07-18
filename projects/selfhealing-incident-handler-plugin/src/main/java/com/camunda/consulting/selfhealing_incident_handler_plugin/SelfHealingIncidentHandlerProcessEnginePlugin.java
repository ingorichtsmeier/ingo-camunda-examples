package com.camunda.consulting.selfhealing_incident_handler_plugin;

import java.util.Arrays;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.incident.IncidentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfHealingIncidentHandlerProcessEnginePlugin implements ProcessEnginePlugin {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(SelfHealingIncidentHandlerProcessEnginePlugin.class);

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    LOGGER.info("Adding EmailIncidentHandler");
    processEngineConfiguration.setCustomIncidentHandlers(Arrays.asList((IncidentHandler) new SelfHealingIncidentHandler("failedJob")));
  }

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
 
  }

  @Override
  public void postProcessEngineBuild(ProcessEngine processEngine) {

  }

}
