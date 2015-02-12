package controllers

import java.net.{ URL, MalformedURLException }

import managers._
import models._

import play.api._
import play.api.libs.json._
import play.api.mvc._

import utils._

import scaldi.{ Injectable, Injector }
import scala.util.control.Exception._

object Shrts {
  private val logger = Logger(this.getClass)
}

// this handles the REST API
class Shrts(implicit inj: Injector) extends Controller with Injectable {
  import Shrts._

  private val manager = inject [ShrtsManager]

  def all = Action {
    val allShrts = manager.listAll()
    Ok(JsArray(allShrts.map { toJson })).as("application/json")
  }

  def popular(k: Int) = Action {
    val populars = manager.mostPopular(k)
    Ok(JsArray(populars.map { toJson })).as("application/json")
  }

  def create = Action { request =>
    val reqJson = request.body.asJson
    logger.debug(s"put: $reqJson")

    (for {
      keyword <- reqJson.map { b => (b \ "keyword").as[String] }
      url <- reqJson.map { b => (b \ "url").as[String] }.flatMap { r => catching(classOf[MalformedURLException]).opt { new URL(r) } }
    } yield {
      val description = reqJson.map { b => (b \ "description").as[String] }
      // TODO val tags: Set[String] = reqJson.flatMap { b => (b \ "tags").as[Set[String]] }
      val shrt = manager.create(keyword, url, description)
      val json: JsValue = toJson(shrt)
      logger.debug(s"New shrt: ${Json.prettyPrint(json)}")
      Ok(json).as("application/json")
    }).getOrElse {
      BadRequest("Missing url and/or keyword!")
    }
  }

  def redirect(token: String) = Action {
    manager.redirect(token) match {
      case Some(shrt) => Redirect(shrt.url.toString).as("application/json")
      case None => NotFound(s"Token $token was not found")
    }
  }

  def delete(token: String) = Action {
    manager.delete(token) match {
      case Some(shrt) => Ok(toJson(shrt)).as("application/json")
      case None => NotFound(s"Token $token was not found")
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
