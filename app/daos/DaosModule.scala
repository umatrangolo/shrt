package daos

import scaldi.Module

class DaosModule extends Module {
  bind[ShrtDao] to new ShrtDaoH2Impl
  bind[HealthcheckDao] to new HealthcheckDaoH2Impl
}
