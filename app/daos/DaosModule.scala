package daos

import scaldi.Module

class DaosModule extends Module {
  bind[ShrtDao] toNonLazy new ShrtDaoH2Impl
  bind[HealthcheckDao] toNonLazy new HealthcheckDaoH2Impl
}
