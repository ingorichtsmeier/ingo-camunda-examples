ngDefine('cockpit.plugin.deployment-plugin', function(module) {

	var DeploymentResource = function ($resource, Uri) {
	  return $resource(Uri.appUri('engine://engine/:engine/deployment'));
	};
	module.factory('DeploymentResource', DeploymentResource);
	
	var ProcessInstanceCountResource = function ($resource, Uri) {
		return $resource(Uri.appUri(
				'engine://engine/:engine/process-instance/count',
				{processDefinitionId: '@processDefinitionId'},
				{query: {method: 'GET', isArray: true, params: {processDefinitionId: ''}}}
		));
	};
	module.factory('ProcessInstanceCountResource', ProcessInstanceCountResource);
	
	var ProcessDefinitionDeploymentResource = function($resource, Uri) {
		return $resource(Uri.appUri(
				'engine://engine/:engine/process-definition', 
				{deploymentId: '@deploymentId'},
				{query: {method: 'GET', isArray: true, params: {deploymentId: ''}}}
		));
	};
	module.factory('ProcessDefinitionDeploymentResource', ProcessDefinitionDeploymentResource);
	
	var DashboardController = function($scope, $http, Uri, 
			DeploymentResource, ProcessDefinitionDeploymentResource, ProcessInstanceCountResource) {
	  // prepare the datepicker
      var today = new Date();
	  $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
      $scope.format = $scope.formats[0];
      $scope.deployedAfter = new Date().setDate(today.getDate()-30);
      $scope.deployedBefore = today;
      $scope.open = function($event, opened) {
    	$event.preventDefault();
    	$event.stopPropagation();
   	    $scope[opened] = true;    	  
   	  };
		  
      $http.get(Uri.appUri("engine://engine/:engine/deployment/count"))
	  .success(function(data) {
		  $scope.deploymentCounter = data;
	  });

	  $scope.deployments = null;

      $scope.findDeployments = function() {
	  var deployments = DeploymentResource.query();
	  console.log(deployments);
	  deployments.$promise.then(function () {
		deployments.forEach(function(deployment, i) {
		  console.log(deployment.id + " " + deployment.name);
		  deployment.runningInstances = 0;
		  ProcessDefinitionDeploymentResource.query({deploymentId: deployment.id})
		  	.$promise.then(function (response) {
			  console.log(response);
			  var processDefinitions = response;
			  deployment.processDefinitions = processDefinitions;
			  processDefinitions.forEach(function(processDefinition, i) {
				console.log(processDefinition);
				ProcessInstanceCountResource.get({processDefinitionId: processDefinition.id})
				  .$promise.then(function (processInstanceCount) {
					  console.log(processInstanceCount);
					  deployment.runningInstances = deployment.runningInstances + processInstanceCount.count;
				  }); 
			  });
		    });
		});  
	  });
	  $scope.deployments = deployments;
      };
	  
	};
  

  DashboardController.$inject = ["$scope", "$http", "Uri", 
                                 "DeploymentResource", 
                                 "ProcessDefinitionDeploymentResource", 
                                 "ProcessInstanceCountResource"];


  var Configuration = function Configuration(ViewsProvider) {

    ViewsProvider.registerDefaultView('cockpit.dashboard', {
      id: 'deployment-plugin',
      label: 'Deployments',
      url: 'plugin://deployment-plugin/static/app/dashboard.html',
      controller: DashboardController,

      // make sure we have a higher priority than the default plugin
      priority: 1
    });
  };

  Configuration.$inject = ['ViewsProvider'];

  module.config(Configuration);

  return module;

});
