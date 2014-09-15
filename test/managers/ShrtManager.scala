package managers

import org.junit.runner._

import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class ShrtManagerSpec extends Specification {
  "The Shrt manager" should {
    "create a proper Shrt from a valid URL" in { pending }
    "return an already created Shrt if the target URL was already seen" in { pending }
    "correctly redirect to the original URL starting from the input token" in { pending }
    "returns a Non if trying to redirect to a never seen before token" in { pending }
    "removes a Shrt" in { pending }
    "liast all shrts" in { pending }
  }
}
