package managers

import daos._
import gens._
import java.lang.RuntimeException
import java.net.URL
import models._
import play.api.Logger
import scala.collection.LinearSeq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }
import scala.util.control.Exception._
import scaldi._

trait ShrtsManager {
  def create(keyword: String, url: URL, description: Option[String] = None, tags: Set[String] = Set.empty[String], token: Option[String] = None): Try[Shrt]
  def redirect(token: String): Option[Shrt]
  def delete(token: String): Option[Shrt]
  def listAll(): LinearSeq[Shrt]
  def mostPopular(k: Int): LinearSeq[Shrt]
  def search(q: String): LinearSeq[Shrt]
}

case class ShrtAlreadyExistsException(url: URL) extends RuntimeException

private[managers] class ShrtManagerImpl(implicit inj: Injector) extends ShrtsManager with Injectable {
  private[this] val logger = Logger(this.getClass)
  private val shrtDao: ShrtDao = inject [ShrtDao]
  private val shrtGen: ShrtGen = inject [ShrtGen]
  private val searchManager = inject [SearchManager]

  override def create(
    keyword: String,
    url: URL,
    description: Option[String] = None,
    tags: Set[String] = Set.empty[String],
    proposedToken: Option[String] = None
  ): Try[Shrt] = Try {

    def validate() = !shrtDao.read(url).isDefined && !(proposedToken.map { shrtDao.read(_).isDefined }.getOrElse(false))

    if (validate()) {
      val token = proposedToken.getOrElse(shrtGen.gen(url))
      val newShrt = Shrt(keyword, url, token, description, tags)
      try {
        shrtDao.save(newShrt)
        searchManager.index(newShrt)
        newShrt
      } catch {
        case ex: Exception => {
          ignoring(classOf[Exception]) { shrtDao.delete(newShrt.token) } // try to rollback on the db
          throw ex
        }
      }
    } else {
      throw new ShrtAlreadyExistsException(url) // trying to create a duplicate Shrt with an already existing URL/token
    }
  }

  override def redirect(token: String): Option[Shrt] = {
    val shrt = shrtDao.read(token)
    Future { shrt.foreach { s => shrtDao.inc(s.token) } }.onFailure { case t => logger.error(s"Error while incrementing hits for ${token}", t) }
    shrt
  }

  override def delete(token: String): Option[Shrt] = shrtDao.delete(token)
  override def listAll(): LinearSeq[Shrt] = shrtDao.all()
  override def mostPopular(k: Int): LinearSeq[Shrt] = shrtDao.topK(k)
  override def search(q: String): LinearSeq[Shrt] = shrtDao.search(q)
}
