package gens

import java.net.URL

import play.api.Logger

trait ShrtGen {
  def gen(url: URL): String
}
