package gens

import java.net.URL

import models.Shrt

// super naive impl: not sure will always returns a unique shrt
private[gens] class ShrtGenRndImpl extends ShrtGen {
  override def gen(url: URL): Shrt = Shrt(url, scala.util.Random.alphanumeric.take(6).toList.mkString)
}
