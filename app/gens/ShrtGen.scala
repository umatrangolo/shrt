package gens

import java.net.URL

import models.Shrt

import play.api.Logger

trait ShrtGen {
  def gen(url: URL): Shrt
}
