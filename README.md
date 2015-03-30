# shrt

URL shortener + analytics + search

## Api

A REST api is provided to shorten and use URLs.

### Create a Shrt

Using Curl to create a Shrt for Wikipedia:

```shell
   $ curl -v -XPUT -H "Content-Type: application/json" \
       -d '{"keyword":"wikipedia", "url":"http://www.wikipedia.org","description":"Wikipedia", "tags":["wikipedia","truth"]}' \
       http://localhost:9000/shrts
   * Hostname was NOT found in DNS cache
   *   Trying ::1...
   * Connected to localhost (::1) port 9000 (#0)
   > PUT /shrts HTTP/1.1
   > User-Agent: curl/7.37.1
   > Host: localhost:9000
   > Accept: */*
   > Content-Type: application/json
   > Content-Length: 83
   >
   * upload completely sent off: 83 out of 83 bytes
   < HTTP/1.1 201 Created
   < Content-Type: application/json
   < Location: http://localhost:9000/shrts/F0q4ab
   < Content-Length: 119
   <
   * Connection #0 to host localhost left intact
   {
     "keyword":"wikipedia",
     "url":"http://www.wikipedia.org",
     "token":"F0q4ab",
     "description":"Wikipedia",
     "tags":["wikipedia", "truth"],
     "count":0
   }
```

The 'token' field is to be used to ask for the redirect. The URL to
use to get the redirect is proposed in the Location header of the HTTP
respsonse. The passed tags will be used to search for Shrts from the
Search UI.

It is also possible to propose a token the being created Shrt will be
stored with:

```shell
    $ curl -v -XPUT -H "Content-Type: application/json" \
         -d '{"keyword":"stackoverflow", "url":"http://stackoverflow.com","description":"Q&A site", "token":"qa"}' \
         http://localhost:9000/shrts
    * Hostname was NOT found in DNS cache
    *   Trying ::1...
    * Connected to localhost (::1) port 9000 (#0)
    > PUT /shrts HTTP/1.1
    > User-Agent: curl/7.37.1
    > Host: localhost:9000
    > Accept: */*
    > Content-Type: application/json
    > Content-Length: 100
    >
    * upload completely sent off: 100 out of 100 bytes
    < HTTP/1.1 201 Created
    < Content-Type: application/json
    < Location: http://localhost:9000/shrts/qa
    < Content-Length: 122
    <
    * Connection #0 to host localhost left intact
    {
      "keyword":"stackoverflow",
      "url":"http://stackoverflow.com",
      "token":"oUAppQ",
      "description":"Q&A site",
      "tags":[],
      "count":0
    }
```

Proper HTTP error code will be returned if there is another Shrt
colliding on the same token (209) or for invalid input (400).

### Use a Shrt

Given a previously created Shrt we can redirect to it as in:

```shell
    $ curl -v -XGET http://localhost:9000/shrts/F0q4ab
    * Hostname was NOT found in DNS cache
    *   Trying ::1...
    * Connected to localhost (::1) port 9000 (#0)
    > GET /shrts/F0q4ab HTTP/1.1
    > User-Agent: curl/7.37.1
    > Host: localhost:9000
    > Accept: */*
    >
    < HTTP/1.1 303 See Other
    < Content-Type: application/json
    < Location: http://www.wikipedia.org
    < Content-Length: 0
    <
    * Connection #0 to host localhost left intact
```

Doing this from a browser rather then Curl will redirect you
automatically on the original page.

### List

To get a list of all known Shrts so far:

```shell
    $ curl -XGET localhost:9000/shrts
    [{"keyword":"stackoverflow","url":"http://stackoverflow.com","token":"qa","description":"Q&A site","tags":[""],"count":0},
     {"keyword":"wikipedia","url":"http://www.wikipedia.org","token":"F0q4ab","description":"Wikipedia","tags":[""],"count":1}]
```

### Most popular Shrts:

To get the top k most popular Shrts:

```shell
    $ curl -XGET localhost:9000/shrts/popular?k=3
    [{"keyword":"hn","url":"http://www.h2n.com","token":"3bgYVQ","description":"This is Hacker News!","tags":[""],"count":2},
    {"keyword":"facebook","url":"http://www.facebook.com","token":"14Nx9S","description":"This is Facebook!","tags":[""],"count":1},
    {"keyword":"gilt4","url":"http://www.gilt6.com","token":"wYrLYh","description":"This is GILT!","tags":[""],"count":1}]

```

### Delete a Shrt

To soft-delete a Shrt using Curl:

```shell
    $ curl -XDELETE localhost:9000/shrts/3bgYVQ
    {"keyword":"hn","url":"http://www.h2n.com","token":"3bgYVQ","description":"This is Hacker News!","tags":[""],"count":2}
```
