angular.module('game', [])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/game/:Id', {
            templateUrl: 'game/game.tpl.html',
            controller: 'GameCtrl'
        });
    }])
    .controller('GameCtrl', ['$scope', '$location', '$routeParams', function($scope, $location, $routeParams) {
        var updateToolbox = function() {
            $scope.undoable = $scope.sketchpad.undoable();
            $scope.redoable = $scope.sketchpad.redoable();
        }

        $.getJSON("/api/game/get", {Id: $routeParams.Id}, function(data) {
            console.log(data);
            $scope.$apply(function() {
                $scope.Type = data.Type;
                $scope.Data = data.Data;
            });
            var options = {
                width: '100%',
                height: '100%'
            };
            if (data.Type == 0) {
                options.editing = true;
            } else {
                options.editing = false;
                options.strokes = $.parseJSON(data.Data);
            }

            $scope.sketchpad = Raphael.sketchpad("drawing", options);

            $scope.sketchpad.change(function() {
                $scope.$apply(updateToolbox);
            });

            updateToolbox();
        });

        $scope.undo = function() {
            $scope.sketchpad.undo();
            updateToolbox();
        };
        $scope.redo = function() {
            $scope.sketchpad.redo();
            updateToolbox();
        };
        $scope.clear = function() {
            $scope.sketchpad.clear();
            updateToolbox();
        };
        $scope.cancel = function() {
            $location.path("/queue");
        };
        $scope.done = function() {
            console.log($scope);
            var payload = {
                GameId: $routeParams.Id
            };

            if ($scope.Type == 0) {
                payload.Type = 1;
                payload.Data = $scope.sketchpad.json();
            } else {
                payload.Type = 0;
                payload.Data = $scope.story;
            }
            console.log(payload);
            $.post("/api/game/move", payload, function(data) {
                           data = $.parseJSON(data);
                           if (data.Success) {
                               $scope.$apply(function() {
                                   $location.path("/queue");
                                   console.log($scope.GameId);
                               });
                           }
                       });
        };
    }]);
