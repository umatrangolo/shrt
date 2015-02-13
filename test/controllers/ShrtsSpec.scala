package controllers

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._
import play.api.libs.json._
import utils.test._
import scala.collection.LinearSeq
import java.net.URL

class ShrtsSpec extends PlaySpecification {

  "The ShrtsCmds when decoding a PUT request " should {
    import ShrtsCmds._

    "parse a valid request" in {
      val req = Json.parse("""{"keyword": "google", "url": "http://www.google.com", "description": "This is a test!"}""")
      val actual: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req)
      actual === JsSuccess(PostCreateShrtCmd("google", new URL("http://www.google.com"), Some("This is a test!")))
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
    "parse a PUT with wout a description" in {
      val req = Json.parse("""{"keyword": "google", "url": "http://www.google.com", "description": ""}""")
      val req2 = Json.parse("""{"keyword": "google", "url": "http://www.google.com" }""")
      val actual: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req)
      val actual2: JsResult[PostCreateShrtCmd] = PostCreateShrtCmdReads.reads(req2)
      actual === JsSuccess(PostCreateShrtCmd("google", new URL("http://www.google.com"), Some("")))
      actual2 === JsSuccess(PostCreateShrtCmd("google", new URL("http://www.google.com"), None))
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
        "description" -> JsString("This is Gmail")
      ))))
      status(result) must equalTo(201)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"url\":\"https://mail.google.com\"")
    }
    "return a 400 if providing an invalid URL or an empty one and/or no URL at all" in new WithServerAndFakeDb(
      app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
      port = 19000,
      scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    ) {
      val Some(result) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List(
        "keyword" -> JsString("google"),
        "url" -> JsString("www.google.com"),
        "description" -> JsString("This is Google!")
      ))))
      status(result) must equalTo(BAD_REQUEST)

      val Some(result2) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List(
        "keyword" -> JsString("google"),
        "url" -> JsString(""),
        "description" -> JsString("This is Google!")
      ))))
      status(result2) must equalTo(BAD_REQUEST)

      val Some(result3) = route(FakeRequest(PUT, "/shrts").withJsonBody(JsObject(List(
        "keyword" -> JsString("google"),
        "description" -> JsString("This is Google!")
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
        "description" -> JsString("This is Google [REDACTED]!")
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

    // "return a 200 on a delete on an existing token" in new WithServerAndFakeDb(
    //   app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
    //   port = 19000,
    //   scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    // ) {
    //   val Some(result) = route(FakeRequest(DELETE, "/shrts/fcbk"))
    //   status(result) must equalTo(OK)
    //   contentType(result) must beSome("application/json")
    // }
    // "return a 404 on a delete for an unknown token" in new WithServerAndFakeDb(
    //   app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
    //   port = 19000,
    //   scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    // ) {
    //   val Some(result) = route(FakeRequest(DELETE, "/shrts/absent"))
    //   status(result) must equalTo(404)
    // }
    // "return a 200 for a list of all the known tokens" in new WithServerAndFakeDb(
    //   app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
    //   port = 19000,
    //   scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    // ) {
    //   val Some(result) = route(FakeRequest(GET, "/shrts"))
    //   status(result) must equalTo(OK)
    //   contentType(result) must beSome("application/json")
    // }
    // "return the most popular Shrts" in new WithServerAndFakeDb(
    //   app = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
    //   port = 19000,
    //   scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")
    // ) {
    //   val Some(result) = route(FakeRequest(GET, "/shrts/popular?k=3"))
    //   status(result) must equalTo(OK)
    //   contentType(result) must beSome("application/json")
    //   contentAsString(result) must contain("""[{"keyword":"Google","url":"http://www.google.com","token":"googl","tags":["foo","bar"],"count":12},{"keyword":"Twitter","url":"http://www.twitter.com","token":"twttr","tags":["foo","bar"],"count":10},{"keyword":"Facebook","url":"http://www.facebook.com","token":"fcbk","tags":[],"count":9}]""")
    // }
  }
}
