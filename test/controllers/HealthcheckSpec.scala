package controllers

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._

class HealthcheckSpec extends PlaySpecification {

  "The healthcheck controller" should {
    "return OK on an healthy app" in new WithServer(app = FakeApplication(
    additionalConfiguration =
      Helpers.inMemoryDatabase() +
        ("applyEvolutions.test" -> "true") +
        ("db.shrt.url" -> "jdbc:h2:mem:shrt") +
        ("db.shrt.user" -> "sa") +
        ("db.shrt.password" ->"sa")),
      port = 19000) {
      val Some(result)  = route(FakeRequest(GET, "/ping"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("Pong!")
    }
  }
}
