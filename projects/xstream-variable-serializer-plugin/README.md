# Variable Serializer with X-Stream

A Process Engine Plugin for [Camunda BPM](http://docs.camunda.org).

The process engine plugin registers a new Variable Serializer in the process engine. This Serializer uses the X-stream library to save objects as XML and create objects from XML.

It uses a new serialization data format: xstream/xml.

The serializer is implemented in the class [XstreamObjectValueSerializer](src/main/java/com/camunda/consulting/xstreamVariableSerializerPlugin/XstreamObjectValueSerializer.java)  

## How to use it?
The process engine plugin [XstreamVariableSerializerProcessEnginePlugin](src/main/java/com/camunda/consulting/xstreamVariableSerializerPlugin/XstreamVariableSerializerProcessEnginePlugin.java) must be added to the process engine configuration. See the [user guide](http://docs.camunda.org/manual/7.3/guides/user-guide/#process-engine-process-engine-plugins-configuring-process-engine-plugins) how to do it.

Have a look at the tests to see how it works.

## Environment Restrictions
Built and tested against Camunda BPM version 7.3.0.

## Known Limitations

If you just put the jar file from the target directory together with xstream-1.4.8.jar, xpp_-min-1.1.4c.jar and xmlpull-1.1.3.1.jar into the lib folder of the camunda tomcat distribution, you will get an exception 

    com.thoughtworks.xstream.mapper.CannotResolveClassException: com.camunda.consulting.complexObjectProcess.data.Customer
    
Maybe a classloading problem.

## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

<!-- HTML snippet for index page
  <tr>
    <td><img src="snippets/xstream-variable-serializer-plugin/src/main/resources/process.png" width="100"></td>
    <td><a href="snippets/xstream-variable-serializer-plugin">Camunda BPM Process Application</a></td>
    <td>A Process Application for [Camunda BPM](http://docs.camunda.org).</td>
  </tr>
-->