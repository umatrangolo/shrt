(function() {
    var search = angular.module('search', []);

    search.controller('SearchController', ['$http', function($http, $log) {
        var self = this
        $http.get('/shrts/popular?k=10').success(function(data) {
            self.shrts = data;
        });
    }]);

    search.directive('shrt', function() {
        return {
            restrict: 'E',
            templateUrl: '/assets/angularjs/custom/shrt.html'
        };
    });
})();
