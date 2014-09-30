package controllers

import scaldi._

trait WebModule extends Module {
  binding to new Healthcheck
  binding to new Shrts
}
