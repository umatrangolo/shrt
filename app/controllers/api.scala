package controllers

import play.api._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import java.net.URL
import jsons.Jsons._

object ShrtsCmds {
  case class PostCreateShrtCmd(keyword: String, url: URL, description: Option[String] = None, tags: Set[String] = Set.empty[String])

  implicit val PostCreateShrtCmdReads: Reads[PostCreateShrtCmd] = (
    (JsPath \ "keyword").read[String](minLength[String](1)) and
    (JsPath \ "url").read[URL] and
    (JsPath \ "description").readNullable[String] and
    (JsPath \ "tags").readNullable[Set[String]].map { _.getOrElse(Set.empty[String]) }
  )(PostCreateShrtCmd.apply _)
}
