Process Application with embedded engine in JEE
===============================================

These projects include a process application with an embedded engine for a JEE application server. It is packed as an ear file with the process application as war included.

The ear includes the libraries for the engine, the connection adapter to access the thread pool for the job executor and an ejb library with the process engine services.

New Goal
--------

Deploy the ear file on a plain vanilla Oracle Weblogic Server. The Server must only be prepared with a datasource for the process engine. 

Old Goal
----

Deploy the ear file on a plain vanilla glassfish server. The thread pool for job executor of the process engine must be prepared. Use the asadmin command from the command line:

    asadmin> create-threadpool --maxthreadpoolsize 6 --minthreadpoolsize 3 --idletimeout 900 --maxqueuesize 10 embedded-platform-jobexecutor-tp

See [Oracle GlassFish Server 3.1 Administration Guide](http://docs.oracle.com/cd/E18930_01/html/821-2416/ablud.html#ggkwj) for further details.

Old Preparation for automatic deployment
------------------------------------  

You can deploy the ear with maven (see [Maven Glassfish Plugin](https://maven-glassfish-plugin.java.net/)). The plugin is used in the ear. 

The plain glassfish server as zip comes without a password for the admin user (see [Oracle GlassFish Server 3.1 Administration Guide](http://docs.oracle.com/cd/E18930_01/html/821-2416/giubb.html#scrolltoc). You have to set the admin password to get the maven glassfish plugin running. The asadmin subcommand is:

    asadmin> change-admin-password --username admin
    
See [Oracle GlassFish Server 3.1 Security Guide](http://docs.oracle.com/cd/E18930_01/html/821-2435/ghgrp.html#ghgpu) for further details.

We use the domain `domian1`, which comes from extracting the zip archive.

<h3>Maven Deployment on Windows</h3>
To run the maven glassfish plugin from windows, you have to delete the `asadmin` executables for unix. (see [asadmin CreateProcess error=193](http://stackoverflow.com/questions/6525979/maven-glassfish-plugin-asadmin-createprocess-error-193))

Content of the ear file
-----------------------

The ear file is controlled by the pom.xml. It includes the engine libraries in a lib folder. The sequence to start the modules is controlled by the initializeInOrder configuration in the pom (and therefore in the application.xml).

      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-ear-plugin</artifactId>
        <version>2.6</version>
        <configuration>
          <version>6</version>
          <defaultLibBundleDir>lib</defaultLibBundleDir>
          <initializeInOrder>true</initializeInOrder>
        </configuration>
      </plugin>

* The connector to access the thread pool:

    <dependency>
      <groupId>org.camunda.bpm.javaee</groupId>
      <artifactId>camunda-jobexecutor-rar</artifactId>
      <version>${camunda.version}</version>
      <type>rar</type>
    </dependency>

* Configuration of the connector: glassfish-resources.xml 

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE resources PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Resource Definitions//EN" "http://glassfish.org/dtds/glassfish-resources_1_5.dtd">
 
    <resources>
        <resource-adapter-config
          resource-adapter-name="embedded-jee-ear#camunda-jobexecutor-rar-7.2.2-ee"
          thread-pool-ids="embedded-platform-jobexecutor-tp" >
        </resource-adapter-config>

        <connector-connection-pool
            name="platformJobExecutorPool"
            resource-adapter-name="embedded-jee-ear#camunda-jobexecutor-rar-7.2.2-ee"
            connection-definition-name=
            "org.camunda.bpm.container.impl.threading.jca.outbound.JcaExecutorServiceConnectionFactory">
        </connector-connection-pool>

        <connector-resource
            enabled="true"
            pool-name="platformJobExecutorPool"
            jndi-name="eis/embedded/JcaExecutorServiceConnectionFactory">
        </connector-resource>        
    </resources>

* The engine startup services with a message driven bean to access the connector. It is shaded from the camunda-glassfish-service project to insert a glassfish-ejb-jar.xml   

* jndi-printer-ear as a helper project to list all jndi names in the engine.

* A process application with a test process and some jsf-pages to start process instances. 

Deployment
----------

go to the main-folder of the project `embedded-jee`. Run

    mvn package  

It deploys the ear to a running glassfish server into the domain `domain1` (it comes with the default installation). The maven glassfish plugin binds the deployment to the `package` phase.

The undeployment is bind to the `clean` phase. **disabled now, see pom.xml: plugin/executions/execution/phase** 
(If the ear was not deployed sucessfully before, `mvn clean` fails at undeploying.)

Known Issues
------------

* The Bean resolver gets not resolved.
* The engine-rest application answers 404 to each request

Known Issues for glassfish
--------------------------

The executorConnectionFactory can not be injected into the ExecutorServiceBean.  

Reason must be resource-adapter-config in glassfish-resources.xml. The ExecutorServiceBean finds the reference to the connector-resource, the connector-resource gets the reference to the connector-connection-pool. (checked by changing the jndi-names).  

<h3>Useful Links</h3>

* Content of glassfish deployment files [Oracle GlassFish Server 3.1 Application Deployment Guide](http://docs.oracle.com/cd/E18930_01/html/821-2417/toc.html)
* Application-scoped resources, section naming, mapping of JNDI names [Oracle GlassFish Server 3.1 Application Deployment Guide](http://docs.oracle.com/cd/E18930_01/html/821-2417/giydj.html)
* Embedded Resource adapters [Oracle GlassFish Server 3.1 Application Deployment Guide](http://docs.oracle.com/cd/E18930_01/html/821-2417/gilxc.html#bealq)
* Camunda installation guide for glassfish [Install the platform on a vanilla GlassFish](http://docs.camunda.org/7.2/guides/installation-guide/glassfish/#bpm-platform-install-the-platform-on-a-vanilla-glassfish)
* Camunda WebSphere configuration [ibm-ejb-jar-bnd.xml](https://github.com/camunda/camunda-bpm-platform-ee/blob/8835d15199535745240b710af86aa526104878e5/distro/ibmWs8/service/src/main/resources/META-INF/ibm-ejb-jar-bnd.xml)
* Glassfish logging configuration [Oracle GlassFish Server 3.1 Administration Guide](http://docs.oracle.com/cd/E18930_01/html/821-2416/abluk.html#scrolltoc)
* EJB resource lookup [5 Ways to Get Resources in EJB 3](http://javahowto.blogspot.ch/2006/06/5-ways-to-get-resources-in-ejb-3.html)
* Packaging application [The Java EE 6 Tutorial](http://docs.oracle.com/javaee/6/tutorial/doc/bnaby.html)
* Deploying JCA Resource Adapter in an application [Tutorial: Deploying a JCA Resource Adapter for an EJB Application](http://www.pramati.com/docstore/1270006/pramati_jca.htm)
* [Difference between resource-ref & resource-env-ref](http://www.coderanch.com/t/157993/java-EJB-SCBCD/certification/Difference-resource-ref-resource-env)
* Mapping references [Oracle GlassFish Server 3.1 Application Development Guide](http://docs.oracle.com/cd/E18930_01/html/821-2418/beaoa.html#scrolltoc)
* [DPL8007: Unsupported deployment descriptors element message-destination?](https://community.oracle.com/thread/2222105) 
* EJB3.1 Injection and Lookup [References to EJBs Outside Your Application With Oracle WebLogic](http://thegreyblog.blogspot.ch/2010/09/references-to-ejbs-outside-your.html)
* [How to create and look up thread pool resource in GlassFish](http://javahowto.blogspot.de/2011/02/how-to-create-and-look-up-thread-pool.html)
* Resource Injection [The Java EE 6 Tutorial](http://docs.oracle.com/javaee/6/tutorial/doc/bncjk.html)
* JNDI Background [What is the relationship between java:comp/env and java:global?](http://stackoverflow.com/questions/7458114/what-is-the-relationship-between-javacomp-env-and-javaglobal)
* Easy example for using thread pools in JEE[Thread pool configuration for inbound resource adapters on TomEE](http://www.tomitribe.com/blog/2014/10/thread-pool-configuration-for-inbound-resource-adapters-on-tomee/)
* [Building and Deploying Java EE EAR with Maven to Java EE Application Server](http://www.developerscrappad.com/1177/java/java-ee/maven/building-and-deploying-java-ee-ear-with-maven-to-java-ee-application-server-part-1-project-directory-structure-amp-module-generation-through-archetype-generate/)
