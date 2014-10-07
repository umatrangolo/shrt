package models

import java.net.URL

case class Shrt(url: URL, token: String, count: Long = 0) {
  require(token.trim.length > 0)
  require(count >= 0)
}
