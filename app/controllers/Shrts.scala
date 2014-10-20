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

class Shrts(implicit inj: Injector) extends Controller with Injectable {
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
    request.body.asJson
      .map { b => (b \ "uri").as[String] }
      .flatMap { r => catching(classOf[MalformedURLException]).opt { new URL(r) } } match {
      case Some(url) => {
        val shrt = manager.create(url)
        val json: JsValue = toJson(shrt)
        Ok(json).as("application/json")
      }
      case None => BadRequest("Missing url!")
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
    "url" -> JsString(shrt.url.toString),
    "shrt" -> JsString(shrt.token),
    "count" -> JsNumber(shrt.count)
  ))
}
