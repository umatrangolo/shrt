package controllers

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._
import utils.test._
import scala.collection.LinearSeq

class ShrtsSpec extends PlaySpecification {

  "The Shrts controller" should {
    "return a 200 with a Shrt for a given URL" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    )  {
      val Some(result) = route(FakeRequest(PUT, "/shrts/www.google.com"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"url\":\"http://www.google.com\"")
      contentAsString(result) must contain("\"count\":12")
    }
    "return a 303 to the original URL for a given token" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(GET, "/shrts/fcbk"))
      status(result) must equalTo(303)
      contentType(result) must beSome("application/json")
      header("Location", result) must beSome("http://www.facebook.com")
    }
    "return a 404 on a redirect attempt with an unkown token" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(GET, "/shrts/absent"))
      status(result) must equalTo(404)
    }
    "return a 200 on a delete on an existing token" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(DELETE, "/shrts/fcbk"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
    }
    "return a 404 on a delete for an unknown token" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(DELETE, "/shrts/absent"))
      status(result) must equalTo(404)
    }
    "return a 200 for a list of all the known tokens" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(GET, "/shrts"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
    }
  }
}
