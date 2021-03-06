package daos

import anorm.SqlParser._
import anorm._
import java.net.URL
import models.Shrt
import play.api.Logger
import play.api.Play.current
import play.api.db._
import scala.collection.LinearSeq

private[daos] object ShrtDaoH2Impl {
  def encode(tags: Set[String]): String = tags.mkString("#")

  def decode(encoded: Option[String]): Set[String] = encoded.map { _.split("#") } match {
    case Some(tags) => Set(tags: _*)
    case None => Set.empty[String]
  }

  private val rowParser =
    long("id") ~ str("keyword") ~ str("url") ~ str("token") ~ get("description")(Column.columnToOption[String]) ~ get("tags")(Column.columnToOption[String]) ~ long("count")
}

private[daos] class ShrtDaoH2Impl extends ShrtDao {
  private[this] val log = Logger(this.getClass)
  import ShrtDaoH2Impl._

  // TODO how to force a single result ?
  override def read(url: URL): Option[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, keyword, url, token, description, tags, count, created_at from shrts where url = {url} and is_deleted = false")
      .on('url -> url.toString)
      .as(rowParser *)
      .map { case id ~ keyword ~ url ~ token ~ description ~ tags ~ count => Shrt(keyword, new URL(url), token, description, decode(tags), count) }
      .headOption
  }

  // TODO how to force a single result ?
  override def read(token: String): Option[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, keyword, url, token, description, tags, count, created_at from shrts where token = {token} and is_deleted = false")
      .on('token -> token)
      .as(rowParser *)
      .map { case id ~ keyword ~ url ~ token ~ description ~ tags ~ count => Shrt(keyword, new URL(url), token, description, decode(tags), count) }
      .headOption
  }

  override def all(): LinearSeq[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, keyword, url, token, description, tags, count, created_at from shrts where is_deleted = false order by id desc")
      .as(rowParser *)
      .map { case id ~ keyword ~ url ~ token ~ description ~ tags ~ count => Shrt(keyword, new URL(url), token, description, decode(tags), count) }
  }

  override def save(shrt: Shrt): Option[Long] = DB.withConnection("shrt") { implicit conn =>
    SQL("insert into shrts(keyword, url, token, description, tags, count) values ({keyword}, {url}, {token}, {description}, {tags}, {count})")
      .on('keyword -> shrt.keyword, 'url -> shrt.url.toString, 'token -> shrt.token, 'description -> shrt.description, 'tags -> encode(shrt.tags), 'count -> shrt.count)
      .executeInsert()
  }

  override def inc(token: String): Option[Long] = {
    val shrt = read(token)

    if (shrt.isDefined) {
      DB.withConnection("shrt") { implicit conn =>
        SQL("update shrts set count = {count} where token = {token}")
          .on('token -> shrt.get.token, 'count -> (shrt.get.count + 1).toString) // TODO ugly!
          .executeUpdate()
      }
      Some(shrt.get.count + 1)
    } else None
  }

  override def delete(token: String): Option[Shrt] = {
    val shrt = read(token)

    if (shrt.isDefined) {
      DB.withConnection("shrt") { implicit conn =>
        SQL("update shrts set is_deleted = true, deleted_at = {now} where token = {token}")
          .on('now -> new java.util.Date(), 'token -> token) // TODO fix me!
          .executeUpdate()
      }
      shrt
    } else None
  }

  override def topK(k: Int): LinearSeq[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, keyword, url, token, description, tags, count, created_at from shrts where is_deleted = false order by count desc")
      .as(rowParser *)
      .map { case id ~ keyword ~ url ~ token ~ description ~ tags ~ count => Shrt(keyword, new URL(url), token, description, decode(tags), count) }
      .take(k)
  }

  // TODO
  def search(q: String): LinearSeq[Shrt] = LinearSeq.empty[Shrt]
}
