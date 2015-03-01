package gens

import scaldi.Module

class GensModule extends Module {
  bind [ShrtGen] toNonLazy new ShrtGenRndImpl
}
