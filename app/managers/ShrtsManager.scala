package managers

import gens._

import java.net.URL

import models._

import scala.collection.LinearSeq

import store._

trait ShrtsManager {
  def create(url: URL): Shrt
  def redirect(token: String): Option[Shrt]
  def delete(token: String): Option[Shrt]
  def listAll(): LinearSeq[Shrt]
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

  override def delete(token: String): Option[Shrt] = shrtDao.delete(token)

  override def listAll(): LinearSeq[Shrt] = shrtDao.all()
}
