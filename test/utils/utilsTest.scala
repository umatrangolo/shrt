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
  val app: FakeApplication = FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase(name = "shrt")),
  val scripts: LinearSeq[String] = LinearSeq.empty[String]
) extends Around with Scope {
  import DbUtils._

  implicit def implicitApp = app

  override def around[T: AsResult](t: => T): Result = Helpers.running(app) {
    val jdbcUrl = app.additionalConfiguration("db.shrt.url").asInstanceOf[String]
    scripts.foreach { script => runScript(jdbcUrl, script) }
    AsResult.effectively(t)
  }
}

abstract class WithServerAndFakeDb(
  app: FakeApplication,
  port: Int = Helpers.testServerPort,
  val scripts: LinearSeq[String] = LinearSeq.empty[String]
) extends WithServer(app, port) {
  import DbUtils._

  override def around[T: AsResult](t: => T): Result = Helpers.running(TestServer(port, app)) {
    val jdbcUrl = app.additionalConfiguration("db.shrt.url").asInstanceOf[String]
    scripts.foreach { script => runScript(jdbcUrl, script) }
    AsResult.effectively(t)
  }
}

object DbUtils {

  def h2ds(jdbcUrl: String): javax.sql.DataSource = {
    import org.h2.jdbcx.JdbcDataSource

    val ds = new JdbcDataSource()
    ds.setURL(jdbcUrl)
    ds.setUser("sa")
    ds.setPassword("")

    ds
  }

  def runScript(jdbcUrl: String, script: String): Int = {
    println(s"Executing script ${script} ...")

    val conn = h2ds(jdbcUrl).getConnection
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
