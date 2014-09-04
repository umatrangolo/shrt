package utils

import play.api._
import play.api.http.{ HeaderNames, Status }
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

/**
  * Composable logging action to appropriately log request/response.
  *
  * To decorate an action in order to have its request logged just use
  * this pattern in your controller:
  *
  * {{{
  * Logging {
  *   Action {
  *     // code
  *   }
  * }
  * }}}
  *
  * @author umatrangolo@gilt.com
  * @since 6-Aug-2014
  */
case class Logging[A](action: Action[A]) extends Action[A] {
  import LogMessage._

  def apply(request: Request[A]): Future[Result] = {
    Logger.debug("Going to execute %s".format(request))

    val now = System.currentTimeMillis
    val res = action(request) // execute the wrapped action

    res.onComplete {
      case Success(result) => {
        val elapsed = System.currentTimeMillis - now
        statusCode(result) match {
          case code if (is4xx(code)) => Logger.warn(LogMessage(request, result, elapsed))
          case code if (is5xx(code)) => Logger.error(LogMessage(request, result, elapsed))
          case _ => Logger.info(LogMessage(request, result, elapsed))
        }
      }
      // this is not even called but is here to make the pattern matching exaustive
      case Failure(ex) => Logger.error(LogMessage(request))
    }

    res
  }

  private def is4xx(code: Int) = code >= 400 && code <= 499
  private def is5xx(code: Int) = code >= 500 && code <= 599

  lazy val parser = action.parser
}

private[utils] object LogMessage {
  private val IpAddressHeader = "X-Gilt-Remote-Ip"
  private val UnknownIP = "unknown-ip"
  private val UnknownAcceptHeader = "unknown-accept-header"

  def apply(request: Request[_], result: Result, responseTime: Long): String = Array(
    ipAddress(request),
    request,
    acceptHeader(request),
    statusCode(result),
    responseTime
  ).mkString(" ")

  def apply(request: Request[_]): String = Array(
    ipAddress(request),
    request,
    acceptHeader(request),
    500
  ).mkString(" ")

  def ipAddress(request: Request[_]) = request.headers.get(IpAddressHeader).getOrElse(UnknownIP)
  def acceptHeader(request: Request[_]) = request.headers.get(HeaderNames.ACCEPT).getOrElse(UnknownAcceptHeader)
  def statusCode(result: Result) = result.header.status
}
