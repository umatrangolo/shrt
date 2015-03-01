package controllers

import daos.HealthcheckDao
import play.api._
import play.api.mvc._
import scaldi.{ Injectable, Injector }
import utils._

class Healthcheck(implicit jnj: Injector) extends Controller with Injectable {
  private val healthcheckDao = inject [HealthcheckDao]

  def ping = Action {
    if (healthcheckDao.ping) Ok("Pong!").as("application/json") else InternalServerError("App is down")
  }
}
