package jsons

import java.net.URL

import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.Try

object Jsons {
  implicit val URLReads = new Reads[URL] {
    override def reads(json: JsValue): JsResult[URL] = json match {
      case JsString(url) => Try { new URL(url) }.map { JsSuccess(_) }.getOrElse(JsError("Unable to parse URL"))
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
    }
  }
}
