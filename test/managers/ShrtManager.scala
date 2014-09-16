package managers

import daos._
import gens._

import java.net.URL

import models._

import org.junit.runner._

import org.specs2.mock._
import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class ShrtManagerSpec extends Specification with Mockito {
  isolated

  private val mockDao = mock[ShrtDao]
  private val mockGen = mock[ShrtGen]

  private val Google = new URL("http://www.google.com")
  private val Facebook = new URL("http://www.facebook.com")
  private val Twitter = new URL("http://www.twitter.com")

  private val GoogleShrt = Shrt(Google, "googl")

  // mocked shrt gen behavior
  mockGen.gen(Google) returns GoogleShrt
  mockGen.gen(Facebook) returns Shrt(Facebook, "fcbk")
  mockGen.gen(Twitter) returns Shrt(Twitter, "twttr")

  // mocked dao behavior
  mockDao.read(Google) returns Some(Shrt(Google, "googl"))
  mockDao.read(Facebook) returns Some(Shrt(Facebook, "fcbk"))
  mockDao.read(Twitter) returns None
  mockDao.read("fcbk") returns Some(Shrt(Facebook, "fcbk"))
  mockDao.read("twttr") returns None
  mockDao.delete("fcbk") returns Some(Shrt(Facebook, "fcbk"))
  mockDao.all() returns Shrt(Google, "googl") :: Shrt(Facebook, "fcbk") :: Nil

  private val mngr = new ShrtManagerImpl(mockDao, mockGen) // sut

  "The Shrt manager" should {
    "create a proper Shrt from a valid URL" in {
      mngr.create(Twitter) === Shrt(Twitter, "twttr", 0)
      there was one(mockDao).read(Twitter)
      there was one(mockDao).save(Shrt(Twitter, "twttr", 0))
    }
    "return an already created Shrt if the target URL was already seen" in {
      mngr.create(Google) === GoogleShrt
      there was one(mockDao).read(Google)
      there was no(mockDao).save(any[Shrt])
    }
    "correctly redirect to the original URL starting from the input token" in {
      mngr.redirect("fcbk") === Some(Shrt(Facebook, "fcbk", 0))
      there was one(mockDao).inc("fcbk")
    }
    "returns a None if trying to redirect to a never seen before token" in {
      mngr.redirect("twttr") === None
    }
    "removes a Shrt" in {
      mngr.delete("fcbk") === Some(Shrt(Facebook, "fcbk", 0))
      there was one(mockDao).delete("fcbk")
    }
    "list all shrts" in {
      mngr.listAll === Shrt(Google, "googl") :: Shrt(Facebook, "fcbk") :: Nil
    }
  }
}
