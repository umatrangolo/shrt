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
  def index(shrt: Shrt)
}

private[managers] class SearchManagerLuceneImpl(implicit inj: Injector) extends SearchManager with Injectable {
  private[this] val log = Logger(this.getClass)

  private[this] val shrtDao = inject[ShrtDao]
  private[this] val directory = inject[Directory]
  private[this] val analyzer = inject[Analyzer]

  private[this] var indexSearcher = indexAll(shrtDao)

  // Load all Shrt(s) and index them all
  private[managers] def indexAll(shrtDao: ShrtDao): IndexSearcher = {
    val indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer))

    shrtDao.all().map { shrt =>
      log.debug(s"Indexing $shrt ...")
      indexWriter.addDocument(docFrom(shrt))
    }
    indexWriter.close()

    new IndexSearcher(DirectoryReader.open(directory))
  }

  private[managers] def docFrom(shrt: Shrt): Document = {
    val doc = new Document()
    val text: List[String] = shrt.keyword :: shrt.description.getOrElse("") :: shrt.tags.toList

    doc.add(new Field("text", text.mkString(" "), TextField.TYPE_NOT_STORED))
    doc.add(new Field("keyword", shrt.keyword, TextField.TYPE_STORED))
    doc
  }

  override def index(shrt: Shrt) = {
    val indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer))
    indexWriter.addDocument(docFrom(shrt))
    indexWriter.close()
    indexSearcher = new IndexSearcher(DirectoryReader.open(directory)) // make the latest doc visible
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
