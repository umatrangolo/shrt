package managers

import daos._
import gens._

import java.net.URL

import models._

import org.junit.runner._

import org.specs2.mock._
import org.specs2.mutable._
import org.specs2.runner._

import scaldi._

@RunWith(classOf[JUnitRunner])
class ShrtManagerSpec extends Specification with Mockito {
  isolated

  private val mockDao = mock[ShrtDao]
  private val mockGen = mock[ShrtGen]

  private val Google = new URL("http://www.google.com")
  private val Facebook = new URL("http://www.facebook.com")
  private val Twitter = new URL("http://www.twitter.com")

  // mocked shrt gen behavior
  mockGen.gen(Google) returns "googl"
  mockGen.gen(Facebook) returns "fcbk"
  mockGen.gen(Twitter) returns "twttr"

  // mocked dao behavior
  mockDao.read(Google) returns Some(Shrt("Google", Google, "googl"))
  mockDao.read(Facebook) returns Some(Shrt("Facebook", Facebook, "fcbk"))
  mockDao.read(Twitter) returns None
  mockDao.read("fcbk") returns Some(Shrt("Facebook", Facebook, "fcbk"))
  mockDao.read("twttr") returns None
  mockDao.delete("fcbk") returns Some(Shrt("Facebook", Facebook, "fcbk"))
  mockDao.all() returns Shrt("Google", Google, "googl") :: Shrt("Facebook", Facebook, "fcbk") :: Nil
  mockDao.topK(2) returns Shrt("Google", Google, "googl") :: Shrt("Facebook", Facebook, "fcbk") :: Nil

  private val mngr = new ShrtManagerImpl()(inj = new Module {
    bind [ShrtDao] to mockDao
    bind [ShrtGen] to mockGen
  }) // sut

  "The Shrt manager" should {
    "create a proper Shrt from a valid URL" in {
      mngr.create("Twitter", Twitter) === Shrt("Twitter", Twitter, "twttr", count = 0)
      there was one(mockDao).read(Twitter)
      there was one(mockDao).save(Shrt("Twitter", Twitter, "twttr", count = 0))
    }
    "return an already created Shrt if the target URL was already seen" in {
      mngr.create("Google", Google) === Shrt("Google", Google, "googl", count = 0)
      there was one(mockDao).read(Google)
      there was no(mockDao).save(any[Shrt])
    }
    "correctly redirect to the original URL starting from the input token" in {
      mngr.redirect("fcbk") === Some(Shrt("Facebook", Facebook, "fcbk", count = 0))
      there was one(mockDao).inc("fcbk")
    }
    "returns a None if trying to redirect to a never seen before token" in {
      mngr.redirect("twttr") === None
    }
    "removes a Shrt" in {
      mngr.delete("fcbk") === Some(Shrt("Facebook", Facebook, "fcbk", count = 0))
      there was one(mockDao).delete("fcbk")
    }
    "list all shrts" in {
      mngr.listAll === Shrt("Google", Google, "googl") :: Shrt("Facebook", Facebook, "fcbk") :: Nil
    }
    "get the 3 most popular Shrts" in {
      mngr.mostPopular(2) == Shrt("Google", Google, "googl") :: Shrt("Facebook", Facebook, "fcbk") :: Nil
    }
  }
}
