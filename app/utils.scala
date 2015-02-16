package utils

import com.google.common.base.Throwables._
import java.net.{ URL, MalformedURLException }
import play.api.Logger
import play.api._
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object Security {
  def trySanitize(content: String): Option[String] = Some(content) // TODO
}

object URLs {
  val DefaultProtocol = "http://"
  def prependProtocol(url: String) = s"$DefaultProtocol$url"

  // First tries to parse the base URL and on failure will tries again
  // by prepending the default protocol. If both fails we just give
  // up and return None
  def tryParseUrl(url: String): Option[URL] = Try { new URL(url) }.toOption
}

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
