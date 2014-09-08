package controllers

import play.api._
import play.api.mvc._

import store.ShrtDao

import utils._

object Healthcheck extends Controller {
  private val store = ShrtDao()

  def ping = Action {
    if (store.ping) Ok("Pong!") else InternalServerError("App is down")
  }

}
