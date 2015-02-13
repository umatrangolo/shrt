package models

import java.net.URL

case class Shrt(
  keyword: String,
  url: URL,
  token: String,
  description: Option[String] = None,
  tags: Set[String] = Set.empty[String],
  count: Long = 0) {
  require(keyword != null && keyword.trim.size > 0)
  require(token != null && token.trim.size > 0)
  require(count >= 0)
}
