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
      val response = await(WS.url(s"http://localhost:19000/ping").execute())
      response.status must equalTo(OK)
    }
  }
}
