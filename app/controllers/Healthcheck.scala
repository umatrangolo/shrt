package controllers

import play.api._
import play.api.mvc._

import store.HealthcheckDao

import utils._

object Healthcheck extends Controller {
  private val healthcheckDao = HealthcheckDao()

  def ping = Action {
    if (healthcheckDao.ping) Ok("Pong!") else InternalServerError("App is down")
  }

}
