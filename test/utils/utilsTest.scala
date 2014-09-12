package utils.test

import org.specs2.execute.{ AsResult, Result }
import org.specs2.mutable.Around
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.specification.Scope

import play.api.test.Helpers._
import play.api.test._

import scala.collection.LinearSeq

abstract class WithFakeDb(
  val app: FakeApplication = FakeApplication(
    additionalConfiguration =
      Helpers.inMemoryDatabase() +
        ("applyEvolutions.test" -> "true") +
        ("db.shrt.url" -> "jdbc:h2:mem:shrt") +
        ("db.default.user" -> "sa") +
        ("db.default.password" ->"sa")),
  val scripts: LinearSeq[String] = LinearSeq.empty[String]
) extends Around with Scope {
  implicit def implicitApp = app

  override def around[T: AsResult](t: => T): Result = Helpers.running(app) {
    scripts.foreach { runScript }
    AsResult.effectively(t)
  }

  private lazy val h2ds: javax.sql.DataSource = {
    import org.h2.jdbcx.JdbcDataSource

    val jdbcUrl = app.additionalConfiguration("db.default.url").asInstanceOf[String]
    val ds = new JdbcDataSource()
    ds.setURL(jdbcUrl)
    ds.setUser("sa")
    ds.setPassword("sa")

    ds
  }

  private def runScript(script: String): Int = {
    println(s"Executing script ${script} ...")

    val conn = h2ds.getConnection
    val source = scala.io.Source.fromFile(script)
    val ddls = source.mkString
    source.close()

    try {
      val stmt = conn.createStatement
      stmt.executeUpdate(ddls)
    } finally {
      if (conn != null) conn.close
    }
  }
}
