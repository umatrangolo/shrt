package controllers

import java.net.{ URL, MalformedURLException }
import jsons.Jsons._
import managers._
import models.JsonErrors._
import models._
import play.api._
import play.api.libs.json._
import play.api.mvc._
import scala.util.control.Exception._
import scala.util.{ Try, Success, Failure }
import scaldi.{ Injectable, Injector }
import utils._

private[controllers] object Shrts {
  private val logger = Logger(this.getClass)
}

private[controllers] object ShrtsCmds {
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  case class PostCreateShrtCmd(keyword: String, url: URL, description: Option[String] = None)

  implicit val PostCreateShrtCmdReads: Reads[PostCreateShrtCmd] = (
    (JsPath \ "keyword").read[String](minLength[String](1)) and
    (JsPath \ "url").read[URL] and
    (JsPath \ "description").readNullable[String]
  )(PostCreateShrtCmd.apply _)
}

// this handles the REST API
class Shrts(implicit inj: Injector) extends Controller with Injectable {
  import Shrts._
  import ShrtsCmds._

  private val manager = inject [ShrtsManager]

  def all = Action {
    val allShrts = manager.listAll()
    Ok(JsArray(allShrts.map { toJson })).as("application/json")
  }

  def popular(k: Int) = Action {
    val populars = manager.mostPopular(k)
    Ok(JsArray(populars.map { toJson })).as("application/json")
  }

  def create = Action(parse.json) { request =>
    Json.fromJson[PostCreateShrtCmd](request.body) match {
      case s: JsSuccess[PostCreateShrtCmd] => {
        val cmd = s.get
        val shrt = manager.create(cmd.keyword, cmd.url, cmd.description)
        val resp: JsValue = toJson(shrt) // TODO write a Writes[Shrt]
        logger.debug(s"New shrt: ${Json.prettyPrint(resp)}")
        Created(resp).as("application/json")
      }
      case e: JsError => BadRequest(Json.toJson(InvalidJsonError(e.errors))).as("application/json")
    }
  }

  def redirect(token: String) = Action {
    manager.redirect(token) match {
      case Some(shrt) => Redirect(shrt.url.toString).as("application/json")
      case None => NotFound
    }
  }

  def delete(token: String) = Action {
    manager.delete(token) match {
      case Some(shrt) => Ok(toJson(shrt)).as("application/json")
      case None => NotFound
    }
  }

  private def toJson(shrt: Shrt) = JsObject(Seq(
    "keyword" -> JsString(shrt.keyword),
    "url" -> JsString(shrt.url.toString),
    "token" -> JsString(shrt.token),
    // TODO description
    "tags" -> JsArray(shrt.tags.map { JsString(_) }.toList),
    "count" -> JsNumber(shrt.count)
  ))
}
