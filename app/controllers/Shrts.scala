package controllers

import play.api._
import play.api.mvc._

object Shrts extends Controller {

  def index = Action {
    Ok("shrts goes here!")
  }

  def create(url: String) = Action {
    Ok(s"Creating shrt for $url")
  }

  def redirect(shrt: String) = Action {
    Ok(s"Redirecting with $shrt")
  }

  def delete(shrt: String) = Action {
    Ok(s"Deleting $shrt")
  }
}
