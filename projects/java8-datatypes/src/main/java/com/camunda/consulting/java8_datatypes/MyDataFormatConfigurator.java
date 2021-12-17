package com.camunda.consulting.java8_datatypes;

import org.camunda.spin.impl.json.jackson.format.JacksonJsonDataFormat;
import org.camunda.spin.spi.DataFormatConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class MyDataFormatConfigurator implements DataFormatConfigurator<JacksonJsonDataFormat> {
  
  private static final Logger LOG = LoggerFactory.getLogger(MyDataFormatConfigurator.class); 

  @Override
  public Class<JacksonJsonDataFormat> getDataFormatClass() {
    // TODO Auto-generated method stub
    LOG.info("get the class");
    return JacksonJsonDataFormat.class;
  }

  @Override
  public void configure(JacksonJsonDataFormat dataFormat) {
    // TODO Auto-generated method stub
    JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    dataFormat.setObjectMapper(mapper);
    LOG.info("mapper set to {}", mapper);
  }

}
