package com.pb.gazetteer.lucene;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of ScheduledThreadPoolExecutor that provides access to the submitted Runnable from the queue
 */
class LuceneIndexCleanerExecutor extends ScheduledThreadPoolExecutor
{
	LuceneIndexCleanerExecutor(int corePoolSize)
	{
		super(corePoolSize);
	}

	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task)
	{
		return new LuceneIndexCleanerScheduledFuture<V>(runnable, task);
	}

	/**
	 * Implementation of RunnableScheduledFuture that provides access to the submitted Runnable
	 *
	 * @param <V>
	 */
	static class LuceneIndexCleanerScheduledFuture<V> implements RunnableScheduledFuture<V>
	{
		private final Runnable m_task;
		private final RunnableScheduledFuture<V> m_runnableScheduledFuture;

		private LuceneIndexCleanerScheduledFuture(Runnable task, RunnableScheduledFuture<V> runnableScheduledFuture)
		{
			m_task = task;
			m_runnableScheduledFuture = runnableScheduledFuture;
		}

		/**
		 * @return The runnable that this instance was created with
		 */
		public Runnable getTask()
		{
			return m_task;
		}

		public boolean isPeriodic()
		{
			return m_runnableScheduledFuture.isPeriodic();
		}

		public void run()
		{
			m_runnableScheduledFuture.run();
		}

		public boolean cancel(boolean mayInterruptIfRunning)
		{
			return m_runnableScheduledFuture.cancel(mayInterruptIfRunning);
		}

		public boolean isCancelled()
		{
			return m_runnableScheduledFuture.isCancelled();
		}

		public boolean isDone()
		{
			return m_runnableScheduledFuture.isDone();
		}

		public V get() throws InterruptedException, ExecutionException
		{
			return m_runnableScheduledFuture.get();
		}

		public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
		{
			return m_runnableScheduledFuture.get(timeout, unit);
		}

		public long getDelay(TimeUnit unit)
		{
			return m_runnableScheduledFuture.getDelay(unit);
		}

        @Override
        public int compareTo(Delayed o) {
            if (o == null) {
                throw new NullPointerException();
            }
            return m_runnableScheduledFuture.compareTo(o);
        }


	}
}
