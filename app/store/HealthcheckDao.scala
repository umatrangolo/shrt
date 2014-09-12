package store

import anorm.SqlParser._
import anorm._

import play.api.Logger
import play.api.db._
import play.api.Play.current


trait HealthcheckDao {

  /**
    * Makes sure the DB is reachable.
    *
    * @return true if the DB is there
    */
  def ping(): Boolean
}

object HealthcheckDao {
  private val instance = new HealthcheckDaoH2Impl()
  def apply(): HealthcheckDao = instance
}

private[store] object HealthcheckDaoH2Impl {
  val log = Logger(this.getClass)
}

private[store] class HealthcheckDaoH2Impl extends HealthcheckDao {
  import HealthcheckDaoH2Impl._

  override def ping(): Boolean = DB.withConnection("shrt") { implicit conn =>
    SQL("select count(*) from shrts").execute()
  }
}
