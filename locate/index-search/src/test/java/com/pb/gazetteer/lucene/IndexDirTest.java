package com.pb.gazetteer.lucene;

import com.pb.gazetteer.NoIndexFoundException;
import com.pb.gazetteer.UnitTestUtils;
import com.pb.gazetteer.webservice.LocateException;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IndexDir.class})
@PowerMockIgnore({"javax.management.*"})
public class IndexDirTest
{

    private File baseDir;

    @Before
    public void setUp() throws Exception
    {
        baseDir = UnitTestUtils.createTempDir();
    }

    @After
    public void tearDown() throws Exception
    {
        FileUtils.deleteDirectory(baseDir);
    }

    @Test
    public void createNewIndexDirShouldCreateIndex() throws LocateException
    {
        IndexDir indexDir = IndexDir.createNewIndexDir(baseDir);
        assertTrue(determineIndexFile(indexDir).exists());
        assertFalse(determineReadyFile(indexDir).exists());
    }

    @Test (expected = LocateException.class)
    public void test_createNewFailsMaxTimes() throws Exception
    {
        File mockFile = mock(File.class);
        whenNew(File.class).withParameterTypes(File.class, String.class).withArguments(anyObject(), anyString()).thenReturn(mockFile);
        when(mockFile.mkdir()).thenReturn(false);
        IndexDir.createNewIndexDir(baseDir);
    }

    @Test
    public void markReadyShouldCreateReadyFileWithSuffix() throws LocateException
    {
        IndexDir indexDir = IndexDir.createNewIndexDir(baseDir);
        assertFalse(determineReadyFile(indexDir).exists());
        indexDir.markReady();
        assertTrue(determineReadyFile(indexDir).exists());
    }

    @Test
    public void deleteShouldDeleteReadyFileAndIndexDir() throws LocateException
    {
        IndexDir indexDir = IndexDir.createNewIndexDir(baseDir);
        indexDir.markReady();
        indexDir.delete();
        assertFalse(determineIndexFile(indexDir).exists());
        assertFalse(determineReadyFile(indexDir).exists());
    }


    @Test
    public void deleteShouldNotTouchIndexDirIfReadyFileDeletionFails()
            throws IOException, LocateException
    {
        IndexDir indexDir = IndexDir.createNewIndexDir(baseDir);
        indexDir.markReady();
        FileInputStream fis = new FileInputStream(determineReadyFile(indexDir));
        try
        {
            indexDir.delete();
        } finally
        {
            fis.close();
        }
        //assertTrue(determineIndexFile(indexDir).exists());
        //assertTrue(determineReadyFile(indexDir).exists());
    }

    @Test
    public void deleteShouldSilentlyGiveUpIfIndexDirCannotBeDeleted()
            throws Exception
    {
        IndexDir indexDir = IndexDir.createNewIndexDir(baseDir);
        indexDir.markReady();
        File f = new File(determineIndexFile(indexDir), "test.txt");
        f.createNewFile();
        FileInputStream fis = new FileInputStream(f);
        try
        {
            indexDir.delete();
        } finally
        {
            fis.close();
        }
        //assertTrue(determineIndexFile(indexDir).exists());
        //assertFalse(determineReadyFile(indexDir).exists());
    }

    @Test
    public void deleteShouldFailSilentlyOnBuildLockedIndexes() throws Exception
    {
        IndexDir indexDir = IndexDir.createNewIndexDir(baseDir);
        indexDir.markReady();

        //lock index
        IndexLock lock = indexDir.acquireIndexLock();
        try
        {
            //try and delete it
            indexDir.delete();

            //confirm no deletion step happened
            assertTrue(determineIndexFile(indexDir).exists());
            assertTrue(determineBuildLockFile(indexDir).exists());
            assertTrue(determineReadyFile(indexDir).exists());
        } finally
        {
            if (lock != null)
            {
                lock.release();
            }
        }
    }

    @Test
    public void deleteShouldWorkOnNonBuildLockedIndexes() throws Exception
    {
        IndexDir indexDir = IndexDir.createNewIndexDir(baseDir);
        File buildLockFile = determineBuildLockFile(indexDir);
        //create but don't lock
        buildLockFile.createNewFile();
        indexDir.delete();
        assertFalse(determineIndexFile(indexDir).exists());
        assertFalse(buildLockFile.exists());
    }

    @Test
    public void deleteShouldWorkOnNonReadyIndexes() throws LocateException
    {
        IndexDir indexDir = IndexDir.createNewIndexDir(baseDir);
        assertFalse(determineReadyFile(indexDir).exists());
        assertTrue(determineIndexFile(indexDir).exists());
        indexDir.delete();
        assertFalse(determineIndexFile(indexDir).exists());
    }

    @Test(expected = NoIndexFoundException.class)
    public void getLatestReadyIndex_noIndex() throws LocateException
    {
        IndexDir.getLatestReadyIndex(baseDir, null);
    }

    @Test
    public void getLatestReadyIndex_rightIndex() throws LocateException
    {
        IndexDir indexDir1 = IndexDir.createNewIndexDir(baseDir);
        indexDir1.markReady();
        IndexDir indexDir2 = IndexDir.createNewIndexDir(baseDir);
        indexDir2.markReady();
        IndexDir indexDir3 = IndexDir.createNewIndexDir(baseDir);

        LuceneIndexCleaner cleaner = null;
        IndexDir latest = null;
        try
        {
            cleaner = new LuceneIndexCleaner(60000, 1);
            latest = IndexDir.getLatestReadyIndex(baseDir, cleaner);
        } finally
        {
            if (cleaner != null)
            {
                cleaner.shutdown();
            }
        }

        assertEquals(indexDir2, latest);
    }

    @Test
    public void getLatestReadyIndex_cleaner() throws LocateException
    {
        IndexDir indexDir1 = IndexDir.createNewIndexDir(baseDir);
        indexDir1.markReady();
        IndexDir indexDir2 = IndexDir.createNewIndexDir(baseDir);
        IndexDir indexDir3 = IndexDir.createNewIndexDir(baseDir);
        indexDir3.markReady();

        LuceneIndexCleaner cleaner = mock(LuceneIndexCleaner.class);

        IndexDir.getLatestReadyIndex(baseDir, cleaner);

        verify(cleaner).delete(eq(indexDir2));
        verify(cleaner).delete(eq(indexDir1));
        verify(cleaner, never()).delete(indexDir3);
    }

    @Test
    public void getLuceneDirShouldReturnFSDirectoryWrappingIndexDir() throws LocateException
    {
        IndexDir indexDir = IndexDir.createNewIndexDir(baseDir);
        Directory dir = indexDir.getLuceneDir();
        assertTrue(dir instanceof FSDirectory);
        FSDirectory fsDir = (FSDirectory) dir;
        File f = fsDir.getDirectory();

        assertEquals(determineIndexFile(indexDir), f);
    }

    private File determineReadyFile(IndexDir dir)
    {
        return new File(baseDir, "index.ready." + Integer.toHexString(dir.getSuffix()));
    }

    private File determineIndexFile(IndexDir dir)
    {
        return new File(baseDir, "index.dat." + Integer.toHexString(dir.getSuffix()));
    }

    private File determineBuildLockFile(IndexDir dir)
    {
        return new File(baseDir, "index.lock." + Integer.toHexString(dir.getSuffix()));
    }

}
