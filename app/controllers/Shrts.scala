package controllers

import JsonErrors._
import java.net.URL
import jsons.Jsons._
import managers._
import models._
import play.api._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import scala.util.{ Try, Success, Failure }
import scaldi.{ Injectable, Injector }

object ShrtsApis {
  case class PostCreateShrtCmd(keyword: String, url: URL, description: Option[String] = None, tags: Set[String] = Set.empty[String], token: Option[String])
  case class ShrtHateoas(shrt: Shrt, redirect: URL)

  implicit val PostCreateShrtCmdReads: Reads[PostCreateShrtCmd] = (
    (JsPath \ "keyword").read[String](minLength[String](1)) and
    (JsPath \ "url").read[URL] and
    (JsPath \ "description").readNullable[String] and
    (JsPath \ "tags").readNullable[Set[String]].map { _.getOrElse(Set.empty[String]) } and
    (JsPath \ "token").readNullable[String]
  )(PostCreateShrtCmd.apply _)

  implicit val ShrtHateoasWrites = new Writes[ShrtHateoas] {
    override def writes(shrtHateoas: ShrtHateoas): JsValue = JsObject(Seq(
      "shrt" -> Json.toJson(shrtHateoas.shrt),
      "redirect" -> JsString(shrtHateoas.redirect.toString)
    ))
  }
}

class Shrts(implicit inj: Injector) extends Controller with Injectable {
  import ShrtsApis._

  private[this] val logger = Logger(this.getClass)
  private val manager = inject [ShrtsManager]
  private val searchManager = inject [SearchManager]

  def popular(k: Int) = Action { implicit request =>
    val populars = manager.mostPopular(k)
    Ok(JsArray(populars.map { shrt =>
      Json.toJson(ShrtHateoas(
        shrt,
        new URL(routes.Shrts.redirect(shrt.token).absoluteURL(false))
      ))
    })).as("application/json")
  }

  def search(query: Option[String] = None) = Action { implicit request =>
    val matches = if (query.isEmpty || query.get.trim.isEmpty) { manager.listAll() } else { searchManager.search(query.get) }
    Ok(JsArray(matches.map { shrt =>
      Json.toJson(ShrtHateoas(
        shrt,
        new URL(routes.Shrts.redirect(shrt.token).absoluteURL(false))
      ))
    })).as("application/json")
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
