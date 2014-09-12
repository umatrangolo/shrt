package store

import org.junit.runner._

import org.specs2.mutable._
import org.specs2.runner._

import play.api.test._

import utils.test.WithFakeDb

import scala.collection.LinearSeq

@RunWith(classOf[JUnitRunner])
class HealthcheckDaoSpec extends Specification {

  private val dao = HealthcheckDao()

  "The Healthcheck dao" should {
    "return true iff the db is online" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/healthcheckdaospec.1.sql")) {
      dao.ping() === true
    }

    "throw an exception iff the db in not online `app is down!`" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/healthcheckdaospec.2.sql")) {
      { dao.ping() } must throwA[Exception]
    }
  }
}
