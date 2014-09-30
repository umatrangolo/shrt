package daos

import anorm.SqlParser._
import anorm._

import play.api.Logger
import play.api.db._
import play.api.Play.current

private[daos] object HealthcheckDaoH2Impl {
  val log = Logger(this.getClass)
}

private[daos] class HealthcheckDaoH2Impl extends HealthcheckDao {
  import HealthcheckDaoH2Impl._

  override def ping(): Boolean = DB.withConnection("shrt") { implicit conn =>
    SQL("select count(*) from shrts").execute()
  }
}
