package controllers

import java.net.URL

import managers._
import models._

import play.api._
import play.api.libs.json._
import play.api.mvc._

import utils._

object Shrts extends Controller {

  private val manager = ShrtsManager()

  def all = Action {
    Ok("shrts goes here!")
  }

  def create(url: String) = Action {
    val shrt = manager.create(new URL("http://" + url)) // TODO hack: fix me!
    val json: JsValue = toJson(shrt)
    Ok(json)
  }

  def redirect(token: String) = Action {
    manager.redirect(token) match {
      case Some(shrt) => Redirect(shrt.url.toString)
      case None => NotFound(s"Token $token was not found")
    }
  }

  def delete(token: String) = Action {
    manager.delete(token) match {
      case Some(shrt) => Ok(toJson(shrt))
      case None => NotFound(s"Token $token was not found")
    }
  }

  private def toJson(shrt: Shrt) = JsObject(Seq("url" -> JsString(shrt.url.toString), "shrt" -> JsString(shrt.shrt), "count" -> JsNumber(shrt.count)))
}
