package managers

import daos.ShrtDao
import models.Shrt
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.{ Field, TextField, Document }
import org.apache.lucene.index.{ IndexWriter, IndexWriterConfig, DirectoryReader }
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.queryparser.classic.QueryParser
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

  private[this] val indexSearcher = indexAll(shrtDao)

  // Load all Shrt(s) and index them all
  private[managers] def indexAll(shrtDao: ShrtDao): IndexSearcher = {
    val indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer))

    shrtDao.all().map { shrt =>
      log.debug(s"Indexing $shrt ...")
      val doc = new Document()
      doc.add(new Field("keyword", shrt.keyword, TextField.TYPE_STORED))
      shrt.description.foreach { d => doc.add(new Field("description", d, TextField.TYPE_STORED)) }
      shrt.tags.foreach { t => doc.add(new Field("tag", t, TextField.TYPE_NOT_STORED)) }
      indexWriter.addDocument(doc)
    }

    indexWriter.close()

    new IndexSearcher(DirectoryReader.open(directory))
  }

  override def search(q: String): LinearSeq[Shrt] = {
    log.debug(s"Searching for $q ...")

    val queryParser = new QueryParser("keyword", analyzer)
    val query = queryParser.parse(q)

    val hits = indexSearcher.search(query, null, 1000).scoreDocs
    if (hits.length == 0) { log.info(s"No hits for $query") } else {
      log.info(s"Found ${hits.length} hits for $query")
      hits.map { hit =>
        log.debug(s"hit: $hit")
      }
    }

    LinearSeq.empty[Shrt] // TODO
  }
}
