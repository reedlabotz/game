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
        var userId = hostapp.getUserId();
        $.getJSON("/api/queue/get", { UserId: userId }, function(data) {
            $scope.$apply(function() {
                $scope.games = data;
            });
        });
        $scope.startGame = function() {
            $location.path("/queue/new");
        };
    }])
    .controller('QueueNewCtrl', ['$scope', '$location', function($scope, $location) {
        window.finishedFriendPicker = function(friends) {
            var userId = hostapp.getUserId();
            $.post("/api/game/start",
                   { userId: userId, 
                     players: friends.join(",") },
                   function(data) {
                       data = $.parseJSON(data);
                       $scope.GameId = data.id;
                   });
            
            $scope.startGame = function() {
                $.post("/api/game/move",
                       { UserId: userId, 
                         GameId: $scope.GameId,
                         Type: 0,
                         Data: $scope.story }, 
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
        };
        hostapp.showFriendPicker();
    }]);

