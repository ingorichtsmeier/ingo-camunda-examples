package org.camunda.bpm.cockpit.deploymentPlugin.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginRootResource;
import org.camunda.bpm.cockpit.deploymentPlugin.DeploymentPlugin;

@Path("plugin/" + DeploymentPlugin.ID)
public class DeploymentPluginRootResource extends AbstractPluginRootResource {

  public DeploymentPluginRootResource() {
    super(DeploymentPlugin.ID);
  }

  @Path("{engineName}/process-instance")
  public ProcessInstanceResource getProcessInstanceResource(@PathParam("engineName") String engineName) {
    return subResource(new ProcessInstanceResource(engineName), engineName);
  }
}
