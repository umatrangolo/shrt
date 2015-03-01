package jsons

import java.net.URL
import models.Shrt
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

  implicit val ShrtWrites = new Writes[Shrt] {
    override def writes(shrt: Shrt): JsValue = JsObject(Seq(
      "keyword" -> JsString(shrt.keyword),
      "url" -> JsString(shrt.url.toString),
      "token" -> JsString(shrt.token),
      "description" -> shrt.description.map { JsString(_) }.getOrElse(JsString("")) ,
      "tags" -> JsArray(shrt.tags.map { JsString(_) }.toList),
      "count" -> JsNumber(shrt.count)
    ))
  }
}
