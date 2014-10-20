package controllers

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._
import play.api.libs.json._
import utils.test._
import scala.collection.LinearSeq

class ShrtsSpec extends PlaySpecification {

  "The Shrts controller" should {
    "return a 200 with a Shrt for a given URL wout the protocol part" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    )  {
      val Some(result) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List("uri" -> JsString("http://www.google.com")))))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"url\":\"http://www.google.com\"")
      contentAsString(result) must contain("\"count\":12")
    }
    "return a 200 with a Shrt for a given URL that has a proper protocol" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    )  {
      val Some(result) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List("uri" -> JsString("https://mail.google.com")))))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"url\":\"https://mail.google.com\"")
    }
    "return a 400 if providing an invalid URL" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    )  {
      val Some(result) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List("uri" -> JsString("htp:/www.google.com")))))
      status(result) must equalTo(BAD_REQUEST)
      println(contentAsString(result))
    }
    "return a 400 if providing an URL wout protocol" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    )  {
      val Some(result) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List("uri" -> JsString("www.google.com")))))
      status(result) must equalTo(BAD_REQUEST)
      println(contentAsString(result))
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
    "return the most popular Shrts" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(GET, "/shrts/popular?k=3"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("""[{"url":"http://http://www.google.com","shrt":"googl","count":12},{"url":"http://http://www.twitter.com","shrt":"twttr","count":10},{"url":"http://http://www.facebook.com","shrt":"fcbk","count":9}]""")
    }
  }
}
