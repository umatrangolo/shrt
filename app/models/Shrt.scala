package models

import java.net.URL

case class Shrt(
  keyword: String,
  url: URL,
  token: String,
  description: Option[String] = None,
  tags: Set[String] = Set.empty[String],
  count: Long = 0) {
  require(keyword.trim.length > 0)
  require(token.trim.length > 0)
  require(count >= 0)
}
