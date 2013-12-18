angular.module('game', [])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/game', {
            templateUrl: 'game/game.tpl.html',
            controller: 'GameCtrl'
        });
    }])
    .controller('GameCtrl', ['$scope', '$location', function($scope, $location) {
        var sketchpad = Raphael.sketchpad("drawing", {
            editing: true,
            width: '100%',
            height: '100%'
        });
        sketchpad.change(function() {
            console.log(sketchpad.json());
        });
    }]);
