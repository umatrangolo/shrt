package managers

import daos._

import gens._

import java.net.URL

import models._

import play.api.Logger

import scala.collection.LinearSeq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scaldi._

trait ShrtsManager {
  def create(url: URL): Shrt
  def redirect(token: String): Option[Shrt]
  def delete(token: String): Option[Shrt]
  def listAll(): LinearSeq[Shrt]
}

private[managers] class ShrtManagerImpl(implicit inj: Injector) extends ShrtsManager with Injectable {
  private[this] val logger = Logger(this.getClass)
  private val shrtDao: ShrtDao = inject [ShrtDao]
  private val shrtGen: ShrtGen = inject [ShrtGen]

  override def create(url: URL): Shrt = shrtDao.read(url).getOrElse {
    val shrt = shrtGen.gen(url)
    shrtDao.save(shrt)
    shrt
  }

  override def redirect(token: String): Option[Shrt] = {
    val shrt = shrtDao.read(token)
    Future { shrt.foreach { s => shrtDao.inc(s.shrt) } }.onFailure { case t => logger.error(s"Error while incrementing hits for ${token}", t) }
    shrt
  }

  override def delete(token: String): Option[Shrt] = shrtDao.delete(token)

  override def listAll(): LinearSeq[Shrt] = shrtDao.all()
}
