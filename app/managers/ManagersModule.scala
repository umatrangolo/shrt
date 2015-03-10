package managers

import scaldi.Module
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.store.{ Directory, RAMDirectory }

class ManagersModule extends Module {
  bind [ShrtsManager] toNonLazy new ShrtManagerImpl

  bind [Directory] toNonLazy new RAMDirectory()
  bind [Analyzer] toNonLazy new StandardAnalyzer()
  bind [SearchManager] toNonLazy new SearchManagerLuceneImpl()
}
