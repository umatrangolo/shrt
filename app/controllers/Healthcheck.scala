package controllers

import play.api._
import play.api.mvc._

import daos.HealthcheckDao

import utils._

import scaldi._

class Healthcheck(implicit jnj: Injector) extends Controller with Injectable {
  private val healthcheckDao = inject [HealthcheckDao]

  def ping = Action {
    if (healthcheckDao.ping) Ok("Pong!").as("application/json") else InternalServerError("App is down")
  }
}
