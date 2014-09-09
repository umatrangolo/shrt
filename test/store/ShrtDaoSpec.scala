package store

import org.junit.runner._

import org.specs2.execute.{ AsResult, Result }
import org.specs2.mutable.Around
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.specification.Scope

import play.api.test.Helpers._
import play.api.test._

abstract class WithFakeDb(
  val app: FakeApplication = FakeApplication(
    additionalConfiguration = Helpers.inMemoryDatabase() + ("applyEvolutions.test" -> "true") + ("db.shrt.url" -> "jdbc:h2:mem:shrt")
  )
) extends Around with Scope {
  implicit def implicitApp = app
  override def around[T: AsResult](t: => T): Result = {
    Helpers.running(app) {
      setupDb()
      AsResult.effectively(t)
    }
  }

  // override this to setup your test bed
  def setupDb() {
    // NOP
  }
}

@RunWith(classOf[JUnitRunner])
class ShrtDaoSpec extends Specification {
  "The 'Hello world' string" should {
    "contain 11 characters" in new WithFakeDb {
      override def setupDb() {
        println("=== Setting up my db (1) ===")
      }

      "Hello world" must have size(11)
    }
    "start with 'Hello'" in new WithFakeDb {
      override def setupDb() {
        println("=== Setting up my db (2) ===")
      }

      "Hello world" must startWith("Hello")
    }
    "end with 'world'" in new WithFakeDb {
      override def setupDb() {
        println("=== Setting up my db (3) ===")
      }

      "Hello world" must endWith("world")
    }
  }
}
