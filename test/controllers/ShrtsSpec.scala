package controllers

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._
import play.api.libs.json._
import utils.test._
import scala.collection.LinearSeq
import java.net.URL

class ShrtsSpec extends PlaySpecification {

  "The ShrtsApis when decoding a PUT request " should {
    import ShrtsApis._

    "parse a valid request" in {
      val req = Json.parse("""{"keyword": "google", "url": "http://www.google.com", "description": "This is a test!", "tags": ["foo", "bar"], "token": "baz"}""")
      val actual: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req)
      actual === JsSuccess(PostCreateShrtCmd("google", new URL("http://www.google.com"), Some("This is a test!"), Set("foo", "bar"), Some("baz")))
    }
    "fail to parse a PUT request if no keyword" in {
      val req = Json.parse("""{"url": "http://www.google.com", "description": "This is a test!"}""")
      val actual: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req)
      actual.isError === true
    }
    "fail to parse a PUT if not URL" in {
      val req = Json.parse("""{"keyword": "google", "description": "This is a test!"}""")
      val actual: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req)
      actual.isError === true
    }
    "parse a PUT with and wout a description" in {
      val req = Json.parse("""{"keyword": "google", "url": "http://www.google.com", "description": "", "tags": []}""")
      val req2 = Json.parse("""{"keyword": "google", "url": "http://www.google.com", "tags": [] }""")
      val actual: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req)
      val actual2: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req2)
      actual === JsSuccess(PostCreateShrtCmd("google", new URL("http://www.google.com"), Some(""), Set(), None))
      actual2 === JsSuccess(PostCreateShrtCmd("google", new URL("http://www.google.com"), None, Set(), None))
    }
    "parse a PUT with and wout tags" in {
      val req = Json.parse("""{"keyword": "google", "url": "http://www.google.com", "description": "", "tags": ["foo", "bar"]}""")
      val req2 = Json.parse("""{"keyword": "google", "url": "http://www.google.com", "tags": [] }""")
      val req3 = Json.parse("""{"keyword": "google", "url": "http://www.google.com" }""")
      val actual: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req)
      val actual2: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req2)
      val actual3: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req3)
      actual === JsSuccess(PostCreateShrtCmd("google", new URL("http://www.google.com"), Some(""), Set("foo", "bar"), None))
      actual2 === JsSuccess(PostCreateShrtCmd("google", new URL("http://www.google.com"), None, Set(), None))
      actual3 === JsSuccess(PostCreateShrtCmd("google", new URL("http://www.google.com"), None, Set(), None))
    }
  }

  "The Shrts controller" should {
    // create
    "return a 201 with a Shrt for a given valid URL" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    )  {
      val Some(result) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List(
        "keyword" -> JsString("gmail"),
        "url" -> JsString("https://mail.google.com"),
        "description" -> JsString("This is Gmail"),
        "tags" -> JsArray(Seq(JsString("foo"), JsString("bar"), JsString("baz"))),
        "token" -> JsString("gmil123")
      ))))
      status(result) must equalTo(201)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"url\":\"https://mail.google.com\"")
      contentAsString(result) must contain("\"token\":\"gmil123\"")
    }
    "return a 400 if providing an invalid URL or an empty one and/or no URL at all" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List(
        "keyword" -> JsString("google"),
        "url" -> JsString("www.google.com"),
        "description" -> JsString("This is Google!"),
        "tags" -> JsArray(Seq(JsString("foo"), JsString("bar"), JsString("baz")))
      ))))
      status(result) must equalTo(BAD_REQUEST)

      val Some(result2) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List(
        "keyword" -> JsString("google"),
        "url" -> JsString(""),
        "description" -> JsString("This is Google!"),
        "tags" -> JsArray(Seq(JsString("foo"), JsString("bar"), JsString("baz")))
      ))))
      status(result2) must equalTo(BAD_REQUEST)

      val Some(result3) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List(
        "keyword" -> JsString("google"),
        "description" -> JsString("This is Google!"),
        "tags" -> JsArray(Seq(JsString("foo"), JsString("bar"), JsString("baz")))
      ))))
      status(result3) must equalTo(BAD_REQUEST)
    }
    "return a 409 if trying to create a Shrt with same url" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List(
        "keyword" -> JsString("google"),
        "url" -> JsString("http://www.google.com"),
        "description" -> JsString("This is Google [REDACTED]!"),
        "tags" -> JsArray(Seq(JsString("foo"), JsString("bar")))
      ))))
      status(result) must equalTo(409)
    }

    // redirect
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

    // delete
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

    // populars
    "return the most popular Shrts" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(GET, "/shrts/popular?k=3"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("""[{"shrt":{"keyword":"Google","url":"http://www.google.com","token":"googl","description":"This is Google","tags":["foo","bar"],"count":12},"redirect":"http:/shrts/googl"},{"shrt":{"keyword":"Twitter","url":"http://www.twitter.com","token":"twttr","description":"","tags":["foo","bar"],"count":10},"redirect":"http:/shrts/twttr"},{"shrt":{"keyword":"Facebook","url":"http://www.facebook.com","token":"fcbk","description":"This is Facebook","tags":[],"count":9},"redirect":"http:/shrts/fcbk"}]""")
    }

    // search
    "return a 200 with all known Shrts" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(GET, "/shrts"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
    }

    "return a 200 with all known Shrts if no query given" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(GET, "/shrts?q="))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
    }

    "return a 200 with only matching Shrt(s)" in { pending }
  }
}
