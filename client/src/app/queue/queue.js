angular.module('queue', [])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/queue', {
            templateUrl: 'queue/queue.tpl.html',
            controller: 'QueueCtrl'
        });
    }])
    .controller('QueueCtrl', ['$scope', '$location', function($scope, $location) {
        $scope.startGame = function() {
            $.getJSON("/api/game/start", function(data) {
                if (data.Success) {
                    $location.path("/game/" + data.Id);
                }
            });
        }
    }]);
