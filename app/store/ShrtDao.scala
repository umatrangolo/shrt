package store

import anorm._
import anorm.SqlParser._

import models.Shrt

import play.api.Logger
import play.api.Play.current
import play.api.db._

import java.net.URL

trait ShrtDao {

  /**
    * Makes sure the DB is reachable.
    *
    * @return true if the DB is there
    */
  def ping(): Boolean

  /**
    * Read a Shrt from the original url.
    *
    * @param url the url to find the Shrt for.
    * @return an optional Shrt if found.
    */
  def read(url: URL): Option[Shrt]

  /**
    * Reads a Shrt by its token.
    *
    * @param token the token to look for.
    * @return an optional Shrt if found.
    */
  def read(token: String): Option[Shrt]

  /**
    * Save a Shrt.
    *
    * @param shrt a Shrt to save.
    * @return an optional long with the auto-generated id for the Shrt.
    */
  def save(shrt: Shrt): Option[Long]
}

object ShrtDao {
  private val instance = new ShrtDaoH2Impl()
  def apply(): ShrtDao = instance
}

private[store] object ShrtDaoH2Impl {
  val log = Logger(this.getClass)
}

private[store] class ShrtDaoH2Impl extends ShrtDao {
  import ShrtDaoH2Impl._

  log.info("Starting db ...")

  override def ping(): Boolean = DB.withConnection("shrt") { implicit conn =>
    SQL("select count(*) from shrts").execute()
  }

  override def read(url: URL): Option[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, url, shrt, count, created_at from shrts where url = {url} and is_deleted = false")
      .on('url -> url.toString)
      .as(long("id") ~ str("url") ~ str("shrt") ~ long("count") *)
      .map { case id ~ url ~ shrt ~ count => Shrt(new URL(url), shrt, count) }
      .headOption
  }

  override def read(token: String): Option[Shrt] = DB.withConnection("shrt") { implicit conn =>
    SQL("select id, url, shrt, count, created_at from shrts where shrt = {token} and is_deleted = false")
      .on('token -> token)
      .as(long("id") ~ str("url") ~ str("shrt") ~ long("count") *)
      .map { case id ~ url ~ shrt ~ count => Shrt(new URL(url), shrt, count) }
      .headOption
  }

  override def save(shrt: Shrt): Option[Long] = DB.withConnection("shrt") { implicit conn =>
    SQL("insert into shrts(url, shrt, count) values ({url}, {shrt}, {count})")
      .on('url -> shrt.url.toString, 'shrt -> shrt.shrt, 'count -> shrt.count)
      .executeInsert()
  }
}
