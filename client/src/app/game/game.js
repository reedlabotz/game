angular.module('game', [])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/game', {
            templateUrl: 'game/game.tpl.html',
            controller: 'GameCtrl'
        });
    }])
    .controller('GameCtrl', ['$scope', '$location', function($scope, $location) {
    }]);