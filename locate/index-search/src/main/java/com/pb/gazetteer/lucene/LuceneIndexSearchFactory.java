package com.pb.gazetteer.lucene;

import com.pb.gazetteer.IndexSearchFactory;
import com.pb.gazetteer.LocateInstance;
import com.pb.gazetteer.NoIndexFoundException;

import java.util.Iterator;

/**
 * A factory to create LuceneIndexSearches. It also manages an index mover
 * thread to perform index updates for a single LocateInstance
 */
public class LuceneIndexSearchFactory implements IndexSearchFactory
{
	private final LuceneIndexCleaner m_indexCleaner;

    /**
     *
     * @param delay Time to delay for cleanup of old index. -1 uses default value. unit of millis.
     * @param threadCnt Number of threads in pool servicing cleanup. -1 uses default value.
     */
    public LuceneIndexSearchFactory(int delay, int threadCnt) {
        m_indexCleaner = new LuceneIndexCleaner(delay, threadCnt);
    }

	public LuceneIndexSearch getIndex(LocateInstance locateInstance)
	{
        LuceneInstance luceneInstance = (LuceneInstance) locateInstance;
        /**
         * Reads the latest ready index.
         */
        IndexDir indexDir = luceneInstance.getLatestReadyIndex(m_indexCleaner);
		LuceneIndexSearch index = new LuceneIndexSearch();
		index.setSrs(luceneInstance.getSrs());
		index.setSearchLogic(luceneInstance.getSearchLogic());
		index.setDirectory(indexDir.getLuceneDir());
		return index;
	}

    public void shutdown() {
        m_indexCleaner.shutdown();
    }

}
