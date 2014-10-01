package managers

import gens._

import java.net.URL

import models._

import scala.collection.LinearSeq

import daos._

import scaldi._

trait ShrtsManager {
  def create(url: URL): Shrt
  def redirect(token: String): Option[Shrt]
  def delete(token: String): Option[Shrt]
  def listAll(): LinearSeq[Shrt]
}

private[managers] class ShrtManagerImpl(implicit inj: Injector) extends ShrtsManager with Injectable {
  private val shrtDao: ShrtDao = inject [ShrtDao]
  private val shrtGen: ShrtGen = inject [ShrtGen]

  override def create(url: URL): Shrt = shrtDao.read(url).getOrElse {
    val shrt = shrtGen.gen(url)
    shrtDao.save(shrt)
    shrt
  }

  override def redirect(token: String): Option[Shrt] = {
    val shrt = shrtDao.read(token)
    shrt.foreach { s => shrtDao.inc(s.shrt) }
    shrt
  }

  override def delete(token: String): Option[Shrt] = shrtDao.delete(token)

  override def listAll(): LinearSeq[Shrt] = shrtDao.all()
}
