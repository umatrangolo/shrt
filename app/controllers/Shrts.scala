package controllers

import JsonErrors._
import managers._
import models._
import play.api._
import play.api.libs.json._
import play.api.mvc._
import scala.util.{ Try, Success, Failure }
import scaldi.{ Injectable, Injector }

private[controllers] object Shrts {
  private val logger = Logger(this.getClass)
}

// this handles the REST API
class Shrts(implicit inj: Injector) extends Controller with Injectable {
  import Shrts._
  import ShrtsCmds._

  private val manager = inject [ShrtsManager]

  def all = Action {
    val allShrts = manager.listAll()
    Ok(JsArray(allShrts.map { Json.toJson(_) })).as("application/json")
  }

  def popular(k: Int) = Action {
    val populars = manager.mostPopular(k)
    Ok(JsArray(populars.map { Json.toJson(_) })).as("application/json")
  }

  def create = Action(parse.json) { implicit request =>
    request.body.validate[PostCreateShrtCmd] match {
      case s: JsSuccess[PostCreateShrtCmd] => {
        val cmd = s.get
        manager.create(cmd.keyword, cmd.url, cmd.description, cmd.tags, cmd.token) match {
          case Success(shrt) => {
            val resp: JsValue = Json.toJson(shrt)
            logger.debug(s"New shrt: ${Json.prettyPrint(resp)}")
            Created(resp).as("application/json").withHeaders(LOCATION -> routes.Shrts.redirect(shrt.token).absoluteURL(false))
          }
          case Failure(e) => Status(409)(Json.toJson(ClientError(s"Shrt with url [${cmd.url}] already exists")))
        }
      }
      case e: JsError => BadRequest(Json.toJson(e.errors)).as("application/json")
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
      case Some(shrt) => Ok(Json.toJson(shrt)).as("application/json")
      case None => NotFound
    }
  }
}

// API objects
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import java.net.URL
import jsons.Jsons._

object ShrtsCmds {
  case class PostCreateShrtCmd(keyword: String, url: URL, description: Option[String] = None, tags: Set[String] = Set.empty[String], token: Option[String])

  implicit val PostCreateShrtCmdReads: Reads[PostCreateShrtCmd] = (
    (JsPath \ "keyword").read[String](minLength[String](1)) and
    (JsPath \ "url").read[URL] and
    (JsPath \ "description").readNullable[String] and
    (JsPath \ "tags").readNullable[Set[String]].map { _.getOrElse(Set.empty[String]) } and
    (JsPath \ "token").readNullable[String]
  )(PostCreateShrtCmd.apply _)
}
