package controllers

import play.api.libs.json._
import play.api.data.validation._

case class ServerError(cause: String)
case class ClientError(message: String, errors: Seq[String] = Seq.empty[String])

object JsonErrors {
  implicit val ServerErrorWrites = new Writes[ServerError] {
    override def writes(serverError: ServerError): JsValue = JsObject(Seq(
      "message" -> JsString("Internal Server Error"),
      "error" -> JsString(serverError.cause)
    ))
  }

  implicit val ClientErrorWrites = new Writes[ClientError] {
    override def writes(clientError: ClientError): JsValue = JsObject(Seq(
      "message" -> JsString(clientError.message),
      "errors" -> JsArray(clientError.errors.map { m => JsString(m) })
    ))
  }

  implicit val JsonValidationErrorsWrites = new Writes[Seq[(JsPath, Seq[ValidationError])]] {
    override def writes(validationErrors: Seq[(JsPath, Seq[ValidationError])]): JsValue = JsObject(Seq(
      "message" -> JsString("Invalid JSON"),
      "errors" -> JsArray(
        validationErrors.map { case (path, errors) =>
          JsObject(Seq(path.toString -> JsArray(errors.map { e => JsString(e.message) })))
        })
    ))
  }
}
