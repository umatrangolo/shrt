(function() {
    var search = angular.module('search', []);

    search.controller('SearchController', ['$scope', '$http', '$log', function($scope, $http, $log) {
        var self = this;

        self.shrts = [];
        $http.get('/shrts/popular?k=10').success(function(data) {
            $log.info("Popular shrt are: " + JSON.stringify(data, null, 2));
            self.shrts = data;
        });

        $scope.lookup = function() {
            $log.info("query: " + $scope.query);
            $http.get("/shrts/completions?q=" + $scope.query).success(function(data) {
                $log.info("Completions are: " + JSON.stringify(data));
                var q = _.reduce(data, function(z, e) { return z + "+" + e; }, "");
                $log.info("Searching for: " + q);
                $http.get("/shrts?q=" + q).success(function(data) {
                    $log.info("Matches are: " + JSON.stringify(data, null, 2));
                    self.shrts = data;
                });
            });
        };
    }]);

    search.directive('shrt', function() {
        return {
            restrict: 'E',
            templateUrl: '/assets/angularjs/custom/shrt.html'
        };
    });
})();
