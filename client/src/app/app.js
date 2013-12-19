angular.module('app', [
    'ngRoute',
    'queue',
    'game',
    'templates.app']);

angular.module('app').config(['$routeProvider', function ($routeProvider) {
    $routeProvider.otherwise({redirectTo:'/queue'});
}]);


angular.module('app').controller('AppCtrl', ['$scope', function($scope) {
}]);