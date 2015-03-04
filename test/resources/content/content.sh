#!/bin/sh

curl -v -XPUT -H "Content-Type: application/json" -d '{"keyword":"Facebook", "url":"http://www.facebook.com","description":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus.", "tags":["social", "time-waster"]}' http://localhost:9000/shrts
curl -v -XPUT -H "Content-Type: application/json" -d '{"keyword":"google", "url":"http://www.google.com","description":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. ", "tags":["search"]}' http://localhost:9000/shrts
curl -v -XPUT -H "Content-Type: application/json" -d '{"keyword":"wikipedia", "url":"http://www.wikipedia.org","description":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", "tags":["wikipedia","truth"]}' http://localhost:9000/shrts
