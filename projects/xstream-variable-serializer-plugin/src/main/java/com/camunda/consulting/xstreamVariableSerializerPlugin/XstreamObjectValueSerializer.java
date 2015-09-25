package com.camunda.consulting.xstreamVariableSerializerPlugin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.camunda.bpm.engine.impl.variable.serializer.AbstractObjectValueSerializer;

import com.thoughtworks.xstream.XStream;

public class XstreamObjectValueSerializer extends AbstractObjectValueSerializer {
  
  private static final Logger log = Logger.getLogger(XstreamObjectValueSerializer.class.getName());
  
  private XStream xStream;
  private String XSTREAM_SERIALIZER_NAME = "xstream/xml";

  public XstreamObjectValueSerializer(String serializationDataFormat) {
    super(serializationDataFormat);
    log.info(serializationDataFormat);
    xStream = new XStream();
    xStream.setMode(XStream.NO_REFERENCES);
  }

  @Override
  public String getName() {
    log.info("name wanted: " + XSTREAM_SERIALIZER_NAME);
    return XSTREAM_SERIALIZER_NAME;
  }

  @Override
  protected boolean canSerializeValue(Object value) {
    log.info("yes, I can serialize values");
    return true;
  }

  @Override
  protected byte[] serializeToByteArray(Object deserializedObject) throws Exception {
    String xml = xStream.toXML(deserializedObject);
    log.info("serializeToByteArray: " + xml);
    return xml.getBytes();
  }

  @Override
  protected boolean isSerializationTextBased() {
    log.info("Yes, serialization is text based");
    return true;
  }

  @Override
  protected String getTypeNameForDeserialized(Object deserializedObject) {
    String typedName = deserializedObject.getClass().getName();
    log.info("getTypeNameForDeserialized: " + typedName);
    return typedName;
  }

  @Override
  protected Object deserializeFromByteArray(byte[] object, String objectTypeName) throws Exception {
    ByteArrayInputStream bais = new ByteArrayInputStream(object);
    InputStreamReader inReader = new InputStreamReader(bais, Context.getProcessEngineConfiguration().getDefaultCharset());
    BufferedReader bufferedReader = new BufferedReader(inReader);
    
    try {
      Object deserializedObject = xStream.fromXML(bufferedReader);
      log.info("deserialized From byte array: " + deserializedObject);
      return deserializedObject;
    }
    finally{
      IoUtil.closeSilently(bais);
      IoUtil.closeSilently(inReader);
      IoUtil.closeSilently(bufferedReader);
    }
  }

}
