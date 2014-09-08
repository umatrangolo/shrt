package models

import java.net.URL

case class Shrt(url: URL, shrt: String, count: Long = 0) {
  require(shrt.trim.length > 0)
  require(count >= 0)
}
