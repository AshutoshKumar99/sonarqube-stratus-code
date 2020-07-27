package com.pb.gazetteer.lucene;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest({LuceneIndexCleaner.class, LuceneIndexCleanerExecutor.class})
public class LuceneIndexCleanerTest
{

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_zeroThreads()
    {
        new LuceneIndexCleaner(5, 0);
    }

    @Test
    public void testConstructor_defaultThread() throws Exception
    {
        LuceneIndexCleanerExecutor mockExec = mock(LuceneIndexCleanerExecutor.class);
        whenNew(LuceneIndexCleanerExecutor.class).withArguments(anyInt()).thenReturn(mockExec);

        new LuceneIndexCleaner(5, -1);

        verifyNew(LuceneIndexCleanerExecutor.class).withArguments(eq(2));
    }

    @Test
    public void testConstructor_specifiedThread() throws Exception
    {
        LuceneIndexCleanerExecutor mockExec = mock(LuceneIndexCleanerExecutor.class);
        whenNew(LuceneIndexCleanerExecutor.class).withArguments(anyInt()).thenReturn(mockExec);

        new LuceneIndexCleaner(5, 4);

        verifyNew(LuceneIndexCleanerExecutor.class).withArguments(eq(4));
    }

    @Test
    public void testConstructor_defaultDelay() throws Exception
    {
        IndexDir indexDir = mock(IndexDir.class);
        LuceneIndexCleanerExecutor mockExec = mock(LuceneIndexCleanerExecutor.class);
        whenNew(LuceneIndexCleanerExecutor.class).withArguments(anyInt()).thenReturn(mockExec);
        when(mockExec.schedule((Runnable) any(), eq(300000), eq(TimeUnit.MILLISECONDS))).thenReturn(null);
        when(mockExec.getQueue()).thenReturn(new ArrayBlockingQueue<Runnable>(1));

        LuceneIndexCleaner cleaner = new LuceneIndexCleaner(-1, 4);
        cleaner.delete(indexDir);

        verify(mockExec).schedule((Runnable) any(), eq(300000L), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testConstructor_specifiedDelay() throws Exception
    {
        IndexDir indexDir = mock(IndexDir.class);
        LuceneIndexCleanerExecutor mockExec = mock(LuceneIndexCleanerExecutor.class);
        whenNew(LuceneIndexCleanerExecutor.class).withArguments(anyInt()).thenReturn(mockExec);
        when(mockExec.schedule((Runnable) any(), eq(5), eq(TimeUnit.MILLISECONDS))).thenReturn(null);
        when(mockExec.getQueue()).thenReturn(new ArrayBlockingQueue<Runnable>(1));

        LuceneIndexCleaner cleaner = new LuceneIndexCleaner(5, 4);
        cleaner.delete(indexDir);

        verify(mockExec).schedule((Runnable) any(), eq(5L), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testDelete() throws Exception
    {
        // build an executor with no scheduled tasks
        LuceneIndexCleanerExecutor mockExecutor = buildExecutor(Collections.<IndexDir>emptyList());
        whenNew(LuceneIndexCleanerExecutor.class).withArguments(anyInt()).thenReturn(mockExecutor);

        // schedule a directory for deletion
        LuceneIndexCleaner cleaner = new LuceneIndexCleaner(-1, -1);
        IndexDir mockIndexDir = mock(IndexDir.class);
        cleaner.delete(mockIndexDir);

        // verify the deletion is scheduled
        ArgumentCaptor<LuceneIndexCleanerTask> taskArgumentCaptor = ArgumentCaptor.forClass(LuceneIndexCleanerTask.class);
        verify(mockExecutor).schedule(taskArgumentCaptor.capture(), anyInt(), any(TimeUnit.class));
        assertEquals(mockIndexDir, taskArgumentCaptor.getValue().getIndexDir());
    }

    @Test
    public void testDelete_nonEmpty_nonMatching() throws Exception
    {
        List<IndexDir> existingDirs = new ArrayList<IndexDir>();
        existingDirs.add(mock(IndexDir.class));
        existingDirs.add(mock(IndexDir.class));
        existingDirs.add(mock(IndexDir.class));
        existingDirs.add(mock(IndexDir.class));

        // build an executor with no scheduled tasks
        LuceneIndexCleanerExecutor mockExecutor = buildExecutor(existingDirs);
        whenNew(LuceneIndexCleanerExecutor.class).withArguments(anyInt()).thenReturn(mockExecutor);

        // schedule a directory for deletion
        LuceneIndexCleaner cleaner = new LuceneIndexCleaner(-1, -1);
        IndexDir mockIndexDir = mock(IndexDir.class);
        cleaner.delete(mockIndexDir);

        // verify the deletion is scheduled
        ArgumentCaptor<LuceneIndexCleanerTask> taskArgumentCaptor = ArgumentCaptor.forClass(LuceneIndexCleanerTask.class);
        verify(mockExecutor).schedule(taskArgumentCaptor.capture(), anyInt(), any(TimeUnit.class));
        assertEquals(mockIndexDir, taskArgumentCaptor.getValue().getIndexDir());
    }

    @Test
    public void testDelete_nonEmpty_matching() throws Exception
    {
        IndexDir mockIndexDir = mock(IndexDir.class);

        List<IndexDir> existingDirs = new ArrayList<IndexDir>();
        existingDirs.add(mock(IndexDir.class));
        existingDirs.add(mock(IndexDir.class));
        existingDirs.add(mockIndexDir);
        existingDirs.add(mock(IndexDir.class));
        existingDirs.add(mock(IndexDir.class));

        // build an executor with no scheduled tasks
        LuceneIndexCleanerExecutor mockExecutor = buildExecutor(existingDirs);
        whenNew(LuceneIndexCleanerExecutor.class).withArguments(anyInt()).thenReturn(mockExecutor);

        // schedule the directory for deletion
        LuceneIndexCleaner cleaner = new LuceneIndexCleaner(-1, -1);
        cleaner.delete(mockIndexDir);

        // verify the deletion is scheduled
        verify(mockExecutor, never()).schedule(any(Runnable.class), anyInt(), any(TimeUnit.class));
    }

    private LuceneIndexCleanerExecutor buildExecutor(final Collection<IndexDir> indexDirs)
    {
        final Iterator<IndexDir> iterator = indexDirs.iterator();
        Iterator mockIterator = new Iterator()
        {
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            public Object next()
            {
                LuceneIndexCleanerTask mockTask = mock(LuceneIndexCleanerTask.class);
                when(mockTask.getIndexDir()).thenReturn(iterator.next());

                LuceneIndexCleanerExecutor.LuceneIndexCleanerScheduledFuture mockFuture = mock(LuceneIndexCleanerExecutor.LuceneIndexCleanerScheduledFuture.class);
                when(mockFuture.getTask()).thenReturn(mockTask);
                return mockFuture;
            }

            // not required
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        BlockingQueue mockQueue = mock(BlockingQueue.class);
        when(mockQueue.iterator()).thenReturn(mockIterator);

        LuceneIndexCleanerExecutor mockExecutor = mock(LuceneIndexCleanerExecutor.class);
        when(mockExecutor.getQueue()).thenReturn(mockQueue);

        return mockExecutor;
    }

    @Test
    public void testShutdown() throws Exception
    {
        LuceneIndexCleanerExecutor mockExec = mock(LuceneIndexCleanerExecutor.class);
        whenNew(LuceneIndexCleanerExecutor.class).withArguments(anyInt()).thenReturn(mockExec);

        LuceneIndexCleaner lic = new LuceneIndexCleaner(-1, -1);
        lic.shutdown();

        verify(mockExec).setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        verify(mockExec).shutdown();
    }

}
