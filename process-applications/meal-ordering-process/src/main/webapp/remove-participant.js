var removeParticipantApp = angular.module('removeParticipantApp', []);

removeParticipantApp.controller('RemoveParticipantCtrl', function ($scope, $http) {

  $scope.removeParticipant = function() {
      console.log("remove a participant");
      console.log($scope.trainingID);
      var postdata = {
    	'messageName': 'removeParticipantMessage' ,
    	'businessKey': $scope.trainingID, 
    	'processVariables': {
          'trainingID' : {
             'value':$scope.trainingID, 
             'type':'String'
          },
          'removeParticipant' : {
              'value':$scope.removeParticipantEmail,
              'type':'String'
          }
    	}
      };
      console.log(JSON.stringify(postdata));
      $http.post('/engine-rest/message', postdata)
        .then(function(response) {
          $scope.antwort = response.data[0];
        },
        function(response) {
          alert("error: " + response.data.message);
        });
  };
  
});