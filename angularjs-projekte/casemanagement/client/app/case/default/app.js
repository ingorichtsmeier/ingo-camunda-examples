'use strict';

// require the viewer, make sure you added it to your project
// dependencies via npm install --save-dev bpmn-js
var $ = window.jQuery = window.$ = require('jquery');

var commons = require('camunda-commons-ui/lib');
var CamSDK = require('camunda-commons-ui/vendor/camunda-bpm-sdk-angular');
require('angular-data-depend');
require('./cam-configuration.js');
var angular = require('camunda-commons-ui/vendor/angular');

// include the pages
var routeModule = angular.module('route-module', []);
routeModule.config(require('./list'));

var caseModule = angular.module('cmmn', [commons.name, routeModule.name]);
caseModule.controller('headerCtrl', ['$scope', 'Uri', function($scope, Uri) {
  console.log("header controller");
}]);

// get the camClient
caseModule.factory('camClient', ['Uri', function(Uri) {
  var conf = {
    apiUri: Uri.appUri('engine://'),
    engine: Uri.appUri(':engine')
  };
  return new CamSDK.Client(conf);
}]);

caseModule.controller('listCtrl', ['$scope', '$routeParams', 'camClient', function($scope, $routeParams, camClient) {
  console.log("listCtrl");
  console.log("route params ", $routeParams);

  var ProcessInstance = camClient.resource('process-instance');
  ProcessInstance.list({}, function(err, results) {
    console.log(err);
    console.log("results from ProcessInstance.list", results);
    $scope.$apply(function () {
      $scope.processInstances = results;
    });
  });
}]);

caseModule.controller('detailCtrl', ['$scope', '$routeParams', 'camClient', function($scope, $routeParams, camClient) {
  console.log("detailCtrl");
  var procInstId = $routeParams.id;
  console.log("procInstId: ", procInstId);

  var Variables = camClient.resource('variable');
  Variables.instances({'processInstanceIdIn': [procInstId]}, function(err, results) {
    $scope.$apply(function() {
      $scope.docs = results.filter(function(variable) {
        return variable.type == 'File';
      });
      $scope.vars = results
        .filter(function(variable) {
          return variable.type != 'File';
        })
        .map(function(item) {
          return {
            variable: {
              id:           item.id,
              name:         item.name,
              type:         item.type,
              value:        item.value,
              valueInfo:    item.valueInfo,
              executionId:  item.executionId
            },
            original: item
          };
        });
      $scope.currentDoc = $scope.docs[0];
      $scope.viewDoc = function(doc) {
        $scope.currentDoc = doc;
      }
    });
  });
}]);

// bootstrap without ng-app
angular.element(document).ready(function() {
  angular.bootstrap(document.body,[caseModule.name, 'cam.commons.auth']);
});

function getUri(id) {
  var uri = $('base').attr(id);
  if (!id) {
    throw new Error('Uri base for ' + id + ' could not be resolved');
  }
  return uri;
}

var ModuleConfig = [
  '$routeProvider',
  'UriProvider',
  function(
    $routeProvider,
    UriProvider
  ) {
    $routeProvider.otherwise({ redirectTo: '/list' });

    UriProvider.replace(':appName', 'tasklist'); //just for the credetials
    UriProvider.replace('app://', getUri('href'));
    UriProvider.replace('adminbase://', getUri('app-root') + '/app/admin/');
    UriProvider.replace('admin://', getUri('admin-api')); // + '../admin/'
    UriProvider.replace('engine://', getUri('engine-api'));

    UriProvider.replace(':engine', [ '$window', function($window) {
      var uri = $window.location.href;

      var match = uri.match(/\/app\/case\/([\w-]+)(|\/)/);
      if (match) {
        return match[1];
      } else {
        throw new Error('no process engine selected');
      }
    }]);
  }];

caseModule.config(ModuleConfig);
