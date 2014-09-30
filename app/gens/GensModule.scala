package gens

import scaldi._

trait GensModule extends Module {
  bind[ShrtGen] to new ShrtGenRndImpl()
}
