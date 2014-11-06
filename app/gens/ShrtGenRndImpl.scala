package gens

import java.net.URL

// super naive impl: not sure will always returns a unique token
private[gens] class ShrtGenRndImpl extends ShrtGen {
  override def gen(url: URL): String = scala.util.Random.alphanumeric.take(6).toList.mkString
}
