package com.pb.gazetteer.lucene;

/**
 * A runnable used for deleting a lucene index
 */
class LuceneIndexCleanerTask implements Runnable
{
	private final IndexDir m_indexDir;

	/**
	 * @param indexDir The index to be deleted
	 */
	LuceneIndexCleanerTask(IndexDir indexDir)
	{
		m_indexDir = indexDir;
	}

	public IndexDir getIndexDir()
	{
		return m_indexDir;
	}

	public void run()
	{
		m_indexDir.delete();
	}
}
