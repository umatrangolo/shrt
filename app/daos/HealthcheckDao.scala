package daos

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
