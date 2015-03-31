(function() {
    var search = angular.module('search', []);

    search.controller('SearchController', ['$http', '$log', function($http, $log) {
        var self = this;

        self.shrts = [];
        $http.get('/shrts/popular?k=10').success(function(data) {
            $log.info("Popular shrt are: " + data);
            self.shrts = data;
        });

        this.lookup = function() {
            console.log("!");
            $log.info("query: " + $scope.query);
        };
    }]);

    search.directive('shrt', function() {
        return {
            restrict: 'E',
            templateUrl: '/assets/angularjs/custom/shrt.html'
        };
    });
})();
