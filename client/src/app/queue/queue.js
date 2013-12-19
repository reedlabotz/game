angular.module('queue', [])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/queue', {
            templateUrl: 'queue/queue.tpl.html',
            controller: 'QueueCtrl'
        });
        $routeProvider.when('/queue/new', {
            templateUrl: 'queue/new.tpl.html',
            controller: 'QueueNewCtrl'
        });
    }])
    .controller('QueueCtrl', ['$scope', '$location', function($scope, $location) {
        $scope.startGame = function() {
            $location.path("/queue/new");
        };
    }])
    .controller('QueueNewCtrl', ['$scope', '$location', function($scope, $location) {
        $.post("/api/game/start", function(data) {
            data = $.parseJSON(data);
            if (data.Success) {
                $scope.GameId = data.Id;
            }
        });

        $scope.startGame = function() {
            $.post("/api/game/move",
                       { GameId: $scope.GameId, Type: 0, Data: $scope.story }, 
                       function(data) {
                           data = $.parseJSON(data);
                           if (data.Success) {
                               $scope.$apply(function() {
                                   $location.path("/queue");
                                   console.log($scope.GameId);
                               });
                           }
                       });
        };

        $scope.cancel = function() {
            $location.path("/queue");
        };
    }]);

