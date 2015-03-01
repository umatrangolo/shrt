package daos

import anorm.SqlParser._
import anorm._
import play.api.Logger
import play.api.Play.current
import play.api.db._

private[daos] class HealthcheckDaoH2Impl extends HealthcheckDao {
  private[this] val log = Logger(this.getClass)

  override def ping(): Boolean = DB.withConnection("shrt") { implicit conn =>
    SQL("select count(*) from shrts").execute()
  }
}
