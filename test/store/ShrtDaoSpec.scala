package store

import org.junit.runner._

import org.specs2.execute.{ AsResult, Result }
import org.specs2.mutable.Around
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.specification.Scope

import play.api.test.Helpers._
import play.api.test._

import utils.test.WithFakeDb

import scala.collection.LinearSeq

@RunWith(classOf[JUnitRunner])
class ShrtDaoSpec extends Specification {
  "The 'Hello world' string" should {
    "contain 11 characters" in new WithFakeDb(scripts = LinearSeq("resources/test-1.sql")) {
      "Hello world" must have size(11)
    }
    "start with 'Hello'" in new WithFakeDb {
      "Hello world" must startWith("Hello")
    }
    "end with 'world'" in new WithFakeDb {
      "Hello world" must endWith("world")
    }
  }
}
