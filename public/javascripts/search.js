(function() {
  var search = angular.module('search', []);

  search.controller('SearchController', function() {
    this.shrts = mocks;
  });

  search.directive('shrt', function() {
    return {
      restrict: 'E',
      templateUrl: '/assets/angularjs/custom/shrt.html'
    };
  });

  // TODO
  // Mock data to test the UI
  var mocks = [
    {url:"http://www.microsoft.com",token:"7RWD80",description:"This is Micro$oft",tags:["windows","office","excel"],count:3},
    {url:"http://www.gilt.com",token:"dIqokj",description:"",tags:["fashion","sale"],count:0}
  ];
  // TODO
})();
