package jsons

import Jsons._

import java.net.URL

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._

import play.api.libs.json._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class JsonsSpec extends Specification {
  "Reads[URL]" should {
    "correctly parse a valid URL" in {
      val actual: JsResult[URL] = URLReads.reads(Json.parse("""{"url": "http://www.google.com"}""") \ "url")
      actual === JsSuccess(new URL("http://www.google.com"))
    }
    "fail to parse an empty URL" in {
      val actual: JsResult[URL] = URLReads.reads(Json.parse("""{"url": ""}""") \ "url")
      actual === JsError("Unable to parse URL")
    }
  }
}
