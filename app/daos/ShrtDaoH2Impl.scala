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
    SQL("select id, url, shrt, count, created_at from shrts where url = {url} and is_deleted = false")
      .on('url -> url.toString)
      .as(long("id") ~ str("url") ~ str("shrt") ~ long("count") *)
      .map { case id ~ url ~ shrt ~ count => Shrt(new URL(url), shrt, count) }
      .headOption
  }

  // TODO how to force a single result ?
  override def read(token: String): Option[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, url, shrt, count, created_at from shrts where shrt = {token} and is_deleted = false")
      .on('token -> token)
      .as(long("id") ~ str("url") ~ str("shrt") ~ long("count") *)
      .map { case id ~ url ~ shrt ~ count => Shrt(new URL(url), shrt, count) }
      .headOption
  }

  override def all(): LinearSeq[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, url, shrt, count, created_at from shrts where is_deleted = false order by id desc")
      .as(long("id") ~ str("url") ~ str("shrt") ~ long("count") *)
      .map { case id ~ url ~ shrt ~ count => Shrt(new URL("http://" + url), shrt, count) } // TODO hack: fix me!
  }

  override def save(shrt: Shrt): Option[Long] = DB.withConnection("shrt") { implicit conn =>
    SQL("insert into shrts(url, shrt, count) values ({url}, {shrt}, {count})")
      .on('url -> shrt.url.toString, 'shrt -> shrt.shrt, 'count -> shrt.count)
      .executeInsert()
  }

  override def inc(token: String): Option[Long] = {
    val shrt = read(token)

    if (shrt.isDefined) {
      DB.withConnection("shrt") { implicit conn =>
        SQL("update shrts set count = {count} where shrt = {token}")
          .on('token -> shrt.get.shrt, 'count -> (shrt.get.count + 1).toString) // TODO ugly!
          .executeUpdate()
      }
      Some(shrt.get.count + 1)
    } else None
  }

  override def delete(token: String): Option[Shrt] = {
    val shrt = read(token)

    if (shrt.isDefined) {
      DB.withConnection("shrt") { implicit conn =>
        SQL("update shrts set is_deleted = true, deleted_at = {now} where shrt = {token}")
          .on('now -> new java.util.Date(), 'token -> token) // TODO fix me!
          .executeUpdate()
      }
      shrt
    } else None
  }
}