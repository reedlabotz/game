angular.module('app', [
    'ngRoute',
    'game',
    'templates.app']);

angular.module('app').config(['$routeProvider', function ($routeProvider) {
    $routeProvider.otherwise({redirectTo:'/game'});
}]);


angular.module('app').controller('AppCtrl', ['$scope', function($scope) {
}]);