package managers

import scaldi.Module

class ManagersModule extends Module {
  bind [ShrtsManager] to new ShrtManagerImpl
}
