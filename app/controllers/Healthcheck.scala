package controllers

import play.api._
import play.api.mvc._

import daos.HealthcheckDao

import utils._

object Healthcheck extends Controller {
  private val healthcheckDao = HealthcheckDao()

  def ping = Action {
    if (healthcheckDao.ping) Ok("Pong!").as("application/json") else InternalServerError("App is down")
  }

}
