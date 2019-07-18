# Self Healing Incident Handler Plugin
This process engine plugin invoke a self healing mechanism to resolve the incident automatically. If this didn't completed sucessfully, the incident is shown in the Cockpit.

## Show me the important parts!

A [self healing  incident handler](src/main/java/com/camunda/consulting/selfhealing_incident_handler_plugin/SelfhealingIncidentHandler.java) invokes the self healing logic to handle an indicdent. If the system is healed afterwards, the indicent is resolved. Otherwise the incident will remain.

## How to use it?

To get started refer to `ProcessJunitTest` and `camunda.cfg.xml`.
For using it in production you have to [integrate the plugin into your Camunda BPM configuration](https://docs.camunda.org/manual/latest/user-guide/process-engine/process-engine-plugins/).

## Environment Restrictions
Built and tested against Camunda BPM version 7.11.0.

## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
