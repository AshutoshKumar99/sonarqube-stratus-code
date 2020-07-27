package com.pb.gazetteer.lucene;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LuceneIndexCleanerTaskTest {
	@Test
	public void testGetIndexDir() throws Exception {
		IndexDir mockIndexDir = mock(IndexDir.class);
		assertEquals(mockIndexDir, new LuceneIndexCleanerTask(mockIndexDir).getIndexDir());
	}

	@Test
	public void testRun() throws Exception {
		IndexDir mockIndexDir = mock(IndexDir.class);
		new LuceneIndexCleanerTask(mockIndexDir).run();
		verify(mockIndexDir).delete();
	}
}
