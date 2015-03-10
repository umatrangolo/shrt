package managers

import daos.ShrtDao
import models.Shrt
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.{ Field, TextField, Document }
import org.apache.lucene.index.{ IndexWriter, IndexWriterConfig }
import org.apache.lucene.store.Directory
import play.api.Logger
import scala.collection.LinearSeq
import scaldi._

trait SearchManager {
  def search(q: String): LinearSeq[Shrt]
}

private[managers] class SearchManagerLuceneImpl(implicit inj: Injector) extends SearchManager with Injectable {
  private[this] val log = Logger(this.getClass)

  private[this] val shrtDao = inject[ShrtDao]
  private[this] val directory = inject[Directory]
  private[this] val analyzer = inject[Analyzer]

  def init() = {
    log.info("Initializing Search ...")
    indexAll(shrtDao)
  }

  // Load all Shrt(s) and index them all
  private[managers] def indexAll(shrtDao: ShrtDao) = {
    val indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer))

    shrtDao.all().map { shrt =>
      log.debug(s"Indexing $shrt ...")
      val doc = new Document()
      doc.add(new Field("keyword", shrt.keyword,  TextField.TYPE_STORED)) // TODO index tags and description
      indexWriter.addDocument(doc)
    }
    indexWriter.close()
  }

  override def search(q: String) = ???
}
