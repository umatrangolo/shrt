package daos

import models.Shrt

import java.net.URL

import scala.collection.LinearSeq

trait ShrtDao {

  /**
    * Read a Shrt from the original url.
    *
    * @param url the url to find the Shrt for.
    * @return an optional Shrt if found.
    */
  def read(url: URL): Option[Shrt]

  /**
    * Reads a Shrt by its token.
    *
    * @param token the token to look for.
    * @return an optional Shrt if found.
    */
  def read(token: String): Option[Shrt]

  /**
    * Returns all the stored shrts
    *
    * @return a linear seq (eventually empty) with all the stored shrts.
    */
  // TODO impl pagination
  def all(): LinearSeq[Shrt]

  /**
    * Save a Shrt.
    *
    * @param shrt a Shrt to save.
    * @return an optional long with the auto-generated id for the Shrt.
    */
  def save(shrt: Shrt): Option[Long]

  /**
    * Incs the count on the Shrt with the given token.
    *
    * @param token the unique token of the desired Shrt.
    * @return an optional current count if token was found.
    */
  def inc(token: String): Option[Long]

  /**
    * Soft deleted a Shrt.
    *
    * @param token the unique token for the Shrt
    * @return an optional deleted Shrt if found.
    */
  def delete(token: String): Option[Shrt]
}
