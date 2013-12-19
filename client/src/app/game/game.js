angular.module('game', [])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/game/:Id', {
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
            $scope.$apply(updateToolbox);
        });

        var updateToolbox = function() {
            $scope.undoable = sketchpad.undoable();
            $scope.redoable = sketchpad.redoable();
        }

        $scope.undo = function() {
            sketchpad.undo();
            updateToolbox();
        };
        $scope.redo = function() {
            sketchpad.redo();
            updateToolbox();
        };
        $scope.clear = function() {
            sketchpad.clear();
            updateToolbox();
        };
        updateToolbox();
    }]);
