import com.google.common.base.Throwables._
import controllers._
import daos._
import gens._
import managers._
import models.JsonErrors._
import models._
import play.api.GlobalSettings
import play.api._
import play.api.libs.json._
import play.api.mvc.Results._
import play.api.mvc._
import scala.concurrent.Future
import scaldi.play.ScaldiSupport
import utils._

object Global extends WithFilters(AccessLoggingFilter) with ScaldiSupport {

  override def applicationModule = new WebModule :: new ManagersModule :: new DaosModule :: new GensModule

  override def onStart(app: Application) {
    super.onStart(app)
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info("Application shutdown...")
  }

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = Future.successful {
    InternalServerError(Json.toJson(ServerError(getRootCause(ex).getMessage())))
  }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] = Future.successful {
    NotFound(Json.toJson(ClientError("Not Found")))
  }

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = Future.successful {
    BadRequest(Json.toJson(ClientError("Bad Request", Seq(error))))
  }
}
