package managers

import daos.ShrtDao
import models.Shrt
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.{ Field, TextField, Document }
import org.apache.lucene.index.{ IndexWriter, IndexWriterConfig, DirectoryReader }
import org.apache.lucene.search.{ IndexSearcher, TopDocs }
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

  @volatile private[this] var indexSearcher = indexAll(shrtDao)

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
    doc.add(new Field("token", shrt.token, TextField.TYPE_STORED))
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

    val queryParser = new QueryParser("text", analyzer)
    val query = queryParser.parse(q)

    val hits: TopDocs = indexSearcher.search(query, null, 1000)
    if (hits.totalHits == 0) {
      log.info(s"No hits for $query")
      LinearSeq.empty[Shrt]
    } else {
      val matches = hits.scoreDocs.map { hit => indexSearcher.doc(hit.doc).get("token") }.flatMap { keyword => shrtDao.read(keyword) }
      log.info(s"""Found ${hits.totalHits} hits for query: $query. Matches are:\n${matches.mkString("\n")}""")
      matches.toList
    }
  }
}
