package com.pb.gazetteer.lucene;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LuceneIndexCleanerExecutorTest {

	@Test
	public void testDecorateTask(){
		LuceneIndexCleanerExecutor executor = new LuceneIndexCleanerExecutor(1);

		try {
			Runnable mockTask = mock(Runnable.class);
			executor.schedule(mockTask, 5, TimeUnit.MINUTES);

			BlockingQueue queue = executor.getQueue();
			assertEquals(1, queue.size());

			RunnableScheduledFuture scheduledTask = (RunnableScheduledFuture) queue.iterator().next();
			assertTrue(scheduledTask instanceof LuceneIndexCleanerExecutor.LuceneIndexCleanerScheduledFuture);
			assertEquals(mockTask, ((LuceneIndexCleanerExecutor.LuceneIndexCleanerScheduledFuture)scheduledTask).getTask());
		} finally {
			executor.shutdownNow();
		}
	}
}
