package daos

import scaldi._

trait DaosModule extends Module {
  bind[ShrtDao] to new ShrtDaoH2Impl()
  bind[HealthcheckDao] to new HealthcheckDaoH2Impl()
}
