package gens

import java.net.URL

trait ShrtGen {
  def gen(url: URL): String
}
