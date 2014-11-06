package gens

import java.net.URL

import play.api.Logger
o
trait ShrtGen {
  def gen(url: URL): String
}
