package controllers

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._

class ShrtsSpec extends PlaySpecification {

  "The Shrts controller" should {
    "return a 200 with a Shrt for a given URL" in new WithServer(app = FakeApplication(
      additionalConfiguration =
        Helpers.inMemoryDatabase() +
          ("applyEvolutions.test" -> "true") +
          ("db.shrt.url" -> "jdbc:h2:mem:shrt") +
          ("db.shrt.user" -> "sa") +
          ("db.shrt.password" ->"sa")),
      port = 19000) {
      val Some(result) = route(FakeRequest(PUT, "/shrts/www.google.com"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"url\":\"http://www.google.com\"")
      contentAsString(result) must contain("\"count\":0")
    }
    "return a 301 to the original URL for a given token" in { pending }
    "return a 404 on a redirect attempt with an unkown token" in new WithServer(app = FakeApplication(
      additionalConfiguration =
        Helpers.inMemoryDatabase() +
          ("applyEvolutions.test" -> "true") +
          ("db.shrt.url" -> "jdbc:h2:mem:shrt") +
          ("db.shrt.user" -> "sa") +
          ("db.shrt.password" ->"sa")),
      port = 19000) {
      val Some(result) = route(FakeRequest(GET, "/shrts/absent"))
      status(result) must equalTo(404)
    }
    "return a 200 on a delete on an existing token" in { pending }
    "return a 404 on a delete for an unknown token" in new WithServer(app = FakeApplication(
      additionalConfiguration =
        Helpers.inMemoryDatabase() +
          ("applyEvolutions.test" -> "true") +
          ("db.shrt.url" -> "jdbc:h2:mem:shrt") +
          ("db.shrt.user" -> "sa") +
          ("db.shrt.password" ->"sa")),
      port = 19000) {
      val Some(result) = route(FakeRequest(DELETE, "/shrts/absent"))
      status(result) must equalTo(404)
    }
    "return a 200 for a list of all the known tokens" in { pending }
  }
}
