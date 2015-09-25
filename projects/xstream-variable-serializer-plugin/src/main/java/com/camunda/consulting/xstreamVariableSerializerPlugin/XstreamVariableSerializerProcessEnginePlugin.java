package com.camunda.consulting.xstreamVariableSerializerPlugin;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.variable.serializer.JavaObjectSerializer;
import org.camunda.bpm.engine.impl.variable.serializer.TypedValueSerializer;
import org.camunda.bpm.engine.impl.variable.serializer.VariableSerializers;

public class XstreamVariableSerializerProcessEnginePlugin extends AbstractProcessEnginePlugin {

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
  }

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    VariableSerializers variableSerializers = processEngineConfiguration.getVariableSerializers();
    int javaObjectSerializerIdx = variableSerializers.getSerializerIndexByName(JavaObjectSerializer.NAME);
    TypedValueSerializer<?> xStreamSerializer = new XstreamObjectValueSerializer("xstream/xml");
    variableSerializers.addSerializer(xStreamSerializer , javaObjectSerializerIdx);
  }

  @Override
  public void postProcessEngineBuild(ProcessEngine processEngine) {
  }

}
