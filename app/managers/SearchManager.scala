package managers

import daos.ShrtDao
import models.Shrt
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.{ Field, TextField, Document }
import org.apache.lucene.index.{ IndexWriter, IndexWriterConfig, DirectoryReader }
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.spell.LuceneDictionary
import org.apache.lucene.search.suggest.Lookup
import org.apache.lucene.search.suggest.fst.FSTCompletionLookup
import org.apache.lucene.search.{ IndexSearcher, TopDocs }
import org.apache.lucene.store.Directory
import play.api.Logger
import scala.collection.JavaConverters._
import scala.collection.LinearSeq
import scaldi._

trait SearchManager {
  def search(q: String): LinearSeq[Shrt]
  def lookup(q: String): LinearSeq[String]
  def index(shrt: Shrt)
}

private[managers] class SearchManagerLuceneImpl(implicit inj: Injector) extends SearchManager with Injectable {
  private[this] val log = Logger(this.getClass)

  private[this] val shrtDao = inject[ShrtDao]
  private[this] val directory = inject[Directory]
  private[this] val analyzer = inject[Analyzer]

  @volatile private[this] var (indexSearcher, completions) = indexAll(shrtDao)

  // Load all Shrt(s) and index them all
  private[managers] def indexAll(shrtDao: ShrtDao): (IndexSearcher, Lookup) = {
    val indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer))

    shrtDao.all().map { shrt =>
      log.debug(s"Indexing $shrt ...")
      indexWriter.addDocument(docFrom(shrt))
    }
    indexWriter.close()

    val directoryReader = DirectoryReader.open(directory)
    val dict = new LuceneDictionary(directoryReader, "text")
    val completionLookup = {
      val lookup = new FSTCompletionLookup()
      lookup.build(dict)
      lookup
    }

    (new IndexSearcher(directoryReader), completionLookup)
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
    val directoryReader = DirectoryReader.open(directory)
    val dict = new LuceneDictionary(directoryReader, "text")
    val lookup = {
      val lookup = new FSTCompletionLookup()
      lookup.build(dict)
      lookup
    }

    // make the latest doc visible
    indexSearcher = new IndexSearcher(directoryReader)
    completions = lookup
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

  override def lookup(q: String): LinearSeq[String] = {
    completions.lookup(q, true, 6).asScala.map { c => c.key.toString }.toList
  }
}
