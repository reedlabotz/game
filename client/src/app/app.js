angular.module('app', [
    'ngRoute',
    'queue',
    'game',
    'templates.app']);

angular.module('app').config(['$routeProvider', function ($routeProvider) {
    $routeProvider.otherwise({redirectTo:'/queue'});
}]);


angular.module('app').controller('AppCtrl', ['$scope', function($scope) {
    if (typeof hostapp === 'undefined') {
        console.warn("No hostapp, using debug");
        var userid = prompt("UserId?");
        window.hostapp = {
            getUserId: function() {
                return userid;
            }
        };
    }
}]);