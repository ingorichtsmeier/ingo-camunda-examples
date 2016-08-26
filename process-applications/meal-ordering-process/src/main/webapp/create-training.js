var createTrainingApp = angular.module('createTrainingApp', ['ngAnimate', 'ngSanitize', 'ui.bootstrap']);

createTrainingApp.controller('CreateTrainingCtrl', function ($scope, $http) {

  var participants = [{"name":"Jakob Freund", "email":"Jakob.Freund@camunda.com"}, 
                      {"name":"Bernd Rücker", "email":"bernd.ruecker@Camunda.com"}];
  $scope.participants = participants;

  $scope.startMealOrderingProcess = function() {
      console.log("start a process");
      console.log($scope.training);
      var postdata = {'variables': {
          'training' : {
             'value':angular.toJson($scope.training), 
             'type':'Object',
             'valueInfo': {
                 'serializationDataFormat' : 'application/json',
                 'objectTypeName' : 'com.camunda.consulting.meal_ordering_process.data.Training'
             }
          },
          'participants' : {
              'value':angular.toJson(participants),
              'type':'Object',
              'valueInfo' : {
                   'serializationDataFormat' : 'application/json',
                   'objectTypeName' : 'java.util.ArrayList<com.camunda.consulting.meal_ordering_process.data.Participant>'
              }
          }
      }, 
      'businessKey': $scope.training.trainingID 
      };
      console.log(JSON.stringify(postdata));
      $http.post('/engine-rest/process-definition/key/MealOrderingProcess/start', postdata)
        .then(function(response) {
            console.log(response);
          $scope.answer = "Process started!";
        },
        function(response) {
          alert("error: " + response.data.message);
        });
  };
  
  $scope.today = function() {
    $scope.dt = new Date();
  };
  $scope.today();

  $scope.clear = function() {
    $scope.dt = null;
  };

  $scope.inlineOptions = {
    customClass: getDayClass,
    minDate: new Date(),
    showWeeks: true
  };

  $scope.dateOptions = {
    dateDisabled: disabled,
    formatYear: 'yy',
    maxDate: new Date(2020, 5, 22),
    minDate: new Date(),
    startingDay: 1
  };

  // Disable weekend selection
  function disabled(data) {
    var date = data.date,
      mode = data.mode;
    return mode === 'day' && (date.getDay() === 0 || date.getDay() === 6);
  }

  $scope.toggleMin = function() {
    $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
    $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
  };

  $scope.toggleMin();

  $scope.open1 = function() {
    $scope.popup1.opened = true;
  };

  $scope.open2 = function() {
    $scope.popup2.opened = true;
  };

  $scope.setDate = function(year, month, day) {
    $scope.training.startDate = new Date(year, month, day);
  };

  $scope.format = 'dd.MM.yyyy hh:mm';
  $scope.altInputFormats = ['M!/d!/yyyy'];

  $scope.popup1 = {
    opened: false
  };

  $scope.popup2 = {
    opened: false
  };

  var tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  var afterTomorrow = new Date();
  afterTomorrow.setDate(tomorrow.getDate() + 1);
  $scope.events = [
    {
      date: tomorrow,
      status: 'full'
    },
    {
      date: afterTomorrow,
      status: 'partially'
    }
  ];

  function getDayClass(data) {
    var date = data.date,
      mode = data.mode;
    if (mode === 'day') {
      var dayToCheck = new Date(date).setHours(0,0,0,0);

      for (var i = 0; i < $scope.events.length; i++) {
        var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

        if (dayToCheck === currentDay) {
          return $scope.events[i].status;
        }
      }
    }

    return '';
  }

});