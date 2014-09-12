package store

import org.junit.runner._

import org.specs2.mutable._
import org.specs2.runner._

import play.api.test._

import utils.test.WithFakeDb

import scala.collection.LinearSeq

@RunWith(classOf[JUnitRunner])
class ShrtDaoSpec extends Specification {
  "The 'Hello world' string" should {
    "contain 11 characters" in {
      "Hello world" must have size(11)
    }
    "start with 'Hello'" in {
      "Hello world" must startWith("Hello")
    }
    "end with 'world'" in {
      "Hello world" must endWith("world")
    }
  }
}
