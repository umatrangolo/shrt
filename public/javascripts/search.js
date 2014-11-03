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
    {keyword: "Commons-core", url:"http://ci.gilt.com/jenkins/commons-core/javadoc",token:"7RWD80",description:"Documentation for the Commons Core projects",tags:["gilt", "documentation", "commons"],count:311},
    {keyword: "Microsoft", url:"http://www.microsoft.com",token:"7RWD80",description:"This is Micro$oft",tags:["windows","office","excel"],count:3},
    {keyword: "GILT", url:"http://www.gilt.com",token:"dIqokj",description:"",tags:["fashion","sale"],count:0},
    {keyword: "Facebook", url:"http://www.facebook.com",token:"shfI09j",description:"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec dui felis, sollicitudin luctus sollicitudin ut, placerat commodo massa. Morbi et est lectus. Curabitur suscipit semper dignissim. Maecenas sed augue in nisi fringilla placerat. Etiam enim lorem, rhoncus sed euismod at, lacinia sed erat. Morbi vitae volutpat quam. Quisque cursus velit ante, et pulvinar dolor lobortis feugiat. Nam et mauris ac orci pulvinar sollicitudin eget.",tags:["social","sharing"],count:93}

  ];
  // TODO
})();
