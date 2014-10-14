(function() {
  var search = angular.module('search', []);

  search.controller('SearchController', function() {
    this.shrts = mocks;
  });

  // TODO
  // Mock data to test the UI
  var mocks = [
    {"url":"http://www.microsoft.com","shrt":"7RWD80","count":3},
    {"url":"http://www.gilt.com","shrt":"dIqokj","count":0},
    {"url":"http://www.yahoo.com","shrt":"9vsDBv","count":0},
    {"url":"http://www.google.com","shrt":"QmkGT5","count":0},
    {"url":"http://www.facebook.com","shrt":"vjgjol","count":0}
  ];
  // TODO
})();
