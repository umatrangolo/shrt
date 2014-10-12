package controllers

import managers._
import models._

import play.api._
import play.api.libs.json._
import play.api.mvc._

import scaldi.{ Injectable, Injector }

class Search(implicit inj: Injector) extends Controller with Injectable {
  private val manager = inject [ShrtsManager]

  def search = Action {
    Ok(views.html.search("Shrts"))
  }

}
