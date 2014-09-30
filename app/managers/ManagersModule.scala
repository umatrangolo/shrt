package managers

import scaldi._

import daos._
import gens._

trait ManagersModule extends Module with DaosModule with GensModule {
  bind [ShrtsManager] to injected [ShrtManagerImpl]
}
