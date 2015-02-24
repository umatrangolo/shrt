package daos

import models.Shrt
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test._
import scala.collection.LinearSeq
import scaldi._
import utils.test.WithFakeDb

@RunWith(classOf[JUnitRunner])
class ShrtDaoSpec extends Specification {
  private val dao = new ShrtDaoH2Impl()

  "The ShrtDao" should {
    "read a Shrt from its url" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.read(new java.net.URL("http://www.google.com")) ===
      Some(Shrt("Google", new java.net.URL("http://www.google.com"), "googl", Some("This is Google"), Set("foo", "bar"), 12))
    }
    "read a Shrt from its url and return None iff not found" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.read(new java.net.URL("http://www.absent.com")) === None
    }
    "read a Shrt from its token" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.read("googl") === Some(Shrt("Google", new java.net.URL("http://www.google.com"), "googl", Some("This is Google"), Set("foo", "bar"), 12))
    }
    "read a Shrt form its token and return None iff not found" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.read("absent") === None
    }
    "read all Shrt(s)" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      val actual = dao.all()
      actual.size must equalTo (3)
    }
    "save a Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      val expected = Shrt("Hacker News", new java.net.URL("http://www.hackernews.scom"), "hwught", Some("This is bullshits!"), Set("startup", "money"), 13)
      val id = dao.save(expected)
      id === Some(5)
      dao.read("hwught") === Some(expected)
    }
    "inc the `count` field in a pre-existing Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.inc("fcbk") === Some(10)
      dao.read("fcbk") === Some(Shrt("Facebook", new java.net.URL("http://www.facebook.com"), "fcbk", Some("This is Facebook"), count = 10))
    }
    "return None after trying to inc the `count` field of a non existent Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.inc("absent") === None
    }
    "delete a Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.delete("fcbk") === Some(Shrt("Facebook", new java.net.URL("http://www.facebook.com"), "fcbk", Some("This is Facebook"), count = 9))
      dao.read("fcbk") === None
    }
    "return a None after trying to delete a non existent Shrt" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.delete("absent") === None
    }
    "return the top most accessed K Shrts if any" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.topK(3).map { _.count } === List(12, 10, 9)
      dao.topK(2).map { _.count } === List(12, 10)
      dao.topK(1).map { _.count } === List(12)
      dao.topK(0) === List.empty[Shrt]
      dao.topK(-1) === List.empty[Shrt]
    }
    "return the max possible number of Shrts if there are less than K available" in new WithFakeDb(scripts = LinearSeq("test/resources/sql/shrtdaospec.1.sql")) {
      dao.topK(30).map { _.count } === List(12, 10, 9)
    }
    "return an empty linear seq of Shrts if there are no available" in new WithFakeDb(scripts = Nil) {
      dao.topK(2) === List.empty[Shrt]
    }
  }
}
