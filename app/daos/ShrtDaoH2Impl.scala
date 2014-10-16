package daos

import anorm._
import anorm.SqlParser._

import models.Shrt

import play.api.Logger
import play.api.Play.current
import play.api.db._

import java.net.URL

import scala.collection.LinearSeq

private[daos] object ShrtDaoH2Impl {
  val log = Logger(this.getClass)
}

private[daos] class ShrtDaoH2Impl extends ShrtDao {
  import ShrtDaoH2Impl._

  // TODO how to force a single result ?
  override def read(url: URL): Option[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, url, token, count, created_at from shrts where url = {url} and is_deleted = false")
      .on('url -> url.toString)
      .as(long("id") ~ str("url") ~ str("token") ~ long("count") *)
      .map { case id ~ url ~ token ~ count => Shrt(new URL(url), token, count) }
      .headOption
  }

  // TODO how to force a single result ?
  override def read(token: String): Option[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, url, token, count, created_at from shrts where token = {token} and is_deleted = false")
      .on('token -> token)
      .as(long("id") ~ str("url") ~ str("token") ~ long("count") *)
      .map { case id ~ url ~ token ~ count => Shrt(new URL(url), token, count) }
      .headOption
  }

  override def all(): LinearSeq[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, url, token, count, created_at from shrts where is_deleted = false order by id desc")
      .as(long("id") ~ str("url") ~ str("token") ~ long("count") *)
      .map { case id ~ url ~ token ~ count => Shrt(new URL("http://" + url), token, count) } // TODO hack: fix me!
  }

  override def save(shrt: Shrt): Option[Long] = DB.withConnection("shrt") { implicit conn =>
    SQL("insert into shrts(url, token, count) values ({url}, {token}, {count})")
      .on('url -> shrt.url.toString, 'token -> shrt.token, 'count -> shrt.count)
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
    SQL("select id, url, token, count, created_at from shrts where is_deleted = false order by count desc")
      .as(long("id") ~ str("url") ~ str("token") ~ long("count") *)
      .map { case id ~ url ~ token ~ count => Shrt(new URL("http://" + url), token, count) } // TODO hack: fix me!
      .take(k)
  }
}
