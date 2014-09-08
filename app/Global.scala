import org.apache.commons.lang3.exception.ExceptionUtils._

import play.api._
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future
import utils._
import store._

object Global extends WithFilters(AccessLoggingFilter) {

  override def onStart(app: Application) {
    val shrtDao = ShrtDao()
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
    Future.successful(InternalServerError(
      views.html.error(getStackTrace(ex))
    ))
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    Future.successful(NotFound(
      views.html.notFound(request.path)
    ))
  }

  override def onBadRequest(request: RequestHeader, error: String) = {
    Future.successful(BadRequest(views.html.badRequest(error)))
  }
}
