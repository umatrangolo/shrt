package models

import java.net.URL

case class Shrt(
  keyword: String,
  url: URL,
  token: String,
  description: Option[String] = None,
  tags: Set[String] = Set.empty[String],
  count: Long = 0) {
  require(keyword != null && keyword.trim.size > 0)
  require(token != null && token.trim.size > 0)
  require(count >= 0)
}

object Shrt {
  import play.api.libs.json._
  import play.api.data.validation._

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
