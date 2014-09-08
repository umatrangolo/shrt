package managers

import models._
import store._
import gens._
import java.net.URL

trait ShrtsManager {
  def create(url: URL): Shrt
  def redirect(token: String): Option[Shrt]
}

object ShrtsManager {
  def apply() = new ShrtManagerImpl(ShrtDao(), ShrtGen())
}

private[managers] class ShrtManagerImpl(private val shrtDao: ShrtDao, private val shrtGen: ShrtGen) extends ShrtsManager {
  override def create(url: URL): Shrt = shrtDao.read(url).getOrElse {
    val shrt = shrtGen.gen(url)
    shrtDao.save(shrt)
    shrt
  }

  override def redirect(token: String): Option[Shrt] = shrtDao.read(token)
}
