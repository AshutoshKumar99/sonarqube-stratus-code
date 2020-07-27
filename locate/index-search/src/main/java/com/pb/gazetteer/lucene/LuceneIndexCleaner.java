package com.pb.gazetteer.lucene;

import java.util.concurrent.TimeUnit;

public class LuceneIndexCleaner
{
	private static final int DEFAULT_THREAD_COUNT = 2;
	private static final int DEFAULT_DELAY_VALUE = 300000;
	private static final TimeUnit DEFAULT_DELAY_UNIT = TimeUnit.MILLISECONDS;

	private final LuceneIndexCleanerExecutor m_executor;
    private final int m_cleanupDelayValue;

    /**
     *
     * @param delay Time to delay for cleanup of old index. -1 uses default value. unit of millis.
     * @param threadCnt Number of threads in pool servicing cleanup. -1 uses default value.
     */
    public LuceneIndexCleaner(int delay, int threadCnt) {
        if (threadCnt == 0) {
            throw new IllegalArgumentException("Invalid threadCnt value: " + threadCnt);
        }
        if (threadCnt < 0) {
            threadCnt = DEFAULT_THREAD_COUNT;
        }
        m_executor  = new LuceneIndexCleanerExecutor(threadCnt);

        if (delay < 0) {
            delay = DEFAULT_DELAY_VALUE;
        }
        m_cleanupDelayValue = delay;
    }

    /**
	 * Schedule the indexDir for deletion only if it isn't already scheduled
	 *
	 * @param indexDir
	 */
	public void delete(IndexDir indexDir)
	{
		// see if this cleanup is already scheduled
		for (Runnable future : m_executor.getQueue())
		{
			Runnable task = ((LuceneIndexCleanerExecutor.LuceneIndexCleanerScheduledFuture) future).getTask();
			IndexDir taskIndexDir = ((LuceneIndexCleanerTask) task).getIndexDir();
			if (indexDir.equals(taskIndexDir))
			{
				return;
			}
		}

		// schedule cleanup for new jobs
		m_executor.schedule(new LuceneIndexCleanerTask(indexDir), m_cleanupDelayValue, DEFAULT_DELAY_UNIT);
	}

    public void shutdown() {
        m_executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        m_executor.shutdown();
    }
}
