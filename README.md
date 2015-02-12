shrt
====

URL shortener + analytics + search

# Api

A REST api is provided to short and use URLs.

## Create a Shrt

Using Curl:

    $ curl -XPUT \
      	   -H "Content-Type: application/json" \
	   -d '{"keyword":"gilt", "url":"www.gilt.com","description":"This is GILT!"}' \
	   http://localhost:9000/shrts

This will create a Shrt from the given url and return on success:

    {"url":"http://www.microsoft.com","shrt":"7RWD80","count":0}

The 'shrt' field is the token to be used to ask for the redirect.

## Use a Shrt

Given a previously created Shrt we can redirect to it as in:

    $ curl -v -XGET localhost:9000/shrts/7RWD80

This will read the Shrt and generate a redirect to the original URL. From Curl output:

    < HTTP/1.1 303 See Other
    < Content-Type: application/json
    < Location: http://www.microsoft.com
    < Content-Length: 0

Doing this from a browser will redirect you automatically on the original page.

## List

To get a list of all known Shrts so far:

    $ curl -XGET localhost:9000/shrts

The server will reply back with the list of all known Shrts like:

    [{"url":"http://www.microsoft.com","shrt":"7RWD80","count":3},
     {"url":"http://www.gilt.com","shrt":"dIqokj","count":0},
     {"url":"http://www.yahoo.com","shrt":"9vsDBv","count":0},
     {"url":"http://www.google.com","shrt":"QmkGT5","count":0},
     {"url":"http://www.facebook.com","shrt":"vjgjol","count":0}]

## Delete a Shrt

To soft-delete a Shrt using Curl:

    $ curl -XDELETE localhost:9000/shrts/7RWD80
