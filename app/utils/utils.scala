package utils

import play.api.Logger
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AccessLoggingFilter extends Filter {

  val accessLogger = Logger("access")

  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val now = System.currentTimeMillis
    val result = next(request) // invoke down the chain

    result.foreach { r =>
      val elapsed = System.currentTimeMillis - now
      val msg = s"method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress} status=${r.header.status} time=$elapsed"
      accessLogger.info(msg)
    }

    result
  }
}
