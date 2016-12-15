'use strict';

var fs = require('fs');
var listTemplate = fs.readFileSync(__dirname + '/list.html', 'utf8');
var detailTemplate = fs.readFileSync(__dirname + '/detail.html', 'utf8');


var ListConfig = [ '$routeProvider', function($routeProvider) {
  $routeProvider.when('/list', {
    template: listTemplate,
    controller: 'listCtrl',
    authentication: 'required',
    reloadOnSearch: false
  }).when('/case/:id', {
    template: detailTemplate,
    controller: 'detailCtrl',
    authentication: 'required',
    reloadOnSearch: false
  });
}];

module.exports = ListConfig;
