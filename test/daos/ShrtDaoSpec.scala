package store

import daos.ShrtDao
import models.Shrt

import org.junit.runner._

import org.specs2.mutable._
import org.specs2.runner._

import play.api.test._

import scala.collection.LinearSeq

import utils.test.WithFakeDb

@RunWith(classOf[JUnitRunner])
class ShrtDaoSpec extends Specification {

  private val dao = ShrtDao()

  "The ShrtDao" should {
    "read a Shrt from its url" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.read(new java.net.URL("http://www.google.com")) === Some(Shrt(new java.net.URL("http://www.google.com"), "googl", 12))
    }
    "read a Shrt from its url and return None iff not found" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.read(new java.net.URL("http://www.absent.com")) === None
    }
    "read a Shrt from its token" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.read("googl") === Some(Shrt(new java.net.URL("http://www.google.com"), "googl", 12))
    }
    "read a Shrt form its token and return None iff not found" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.read("absent") === None
    }
    "read all Shrt(s)" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      val actual = dao.all()
      actual.size must equalTo (3)
    }
    "save a Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      val expected = Shrt(new java.net.URL("http://www.hackernews.scom"), "y", 13)
      val id = dao.save(expected)
      id === Some(4)
      dao.read("y") === Some(expected)
    }
    "inc the `count` field in a pre-existing Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.inc("fcbk") === Some(10)
      dao.read("fcbk") === Some(Shrt(new java.net.URL("http://www.facebooks.com"), "fcbk", 10))
    }
    "return None after trying to inc the `count` field of a non existent Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.inc("absent") === None
    }
    "delete a Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.delete("fcbk") === Some(Shrt(new java.net.URL("http://www.facebooks.com"), "fcbk", 9))
      dao.read("fcbk") === None
    }
    "return a None after trying to delete a non existent Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.delete("absent") === None
    }
  }
}
