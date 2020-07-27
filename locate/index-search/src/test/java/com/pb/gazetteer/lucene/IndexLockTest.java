/*******************************************************************************
 * Copyright (c) 2011, Pitney Bowes Software Inc.
 * All  rights reserved.
 * Confidential Property of Pitney Bowes Software Inc.
 *
 * $Author: $
 * $Revision: $
 * $LastChangedDate: $
 *
 * $HeadURL: $
 ******************************************************************************/

package com.pb.gazetteer.lucene;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class IndexLockTest
{
    private static final int TEST_SUFFIX = 5;
    private File m_tmpDir;
    private IndexLock m_indexLock;
    private File m_tmpLockFile;
    private static final String FILE_NAME="IndexLockTest";
    private static final String LOCK_FILE_NAME="index.lock";

    @Before
    public void before() throws IOException, IndexCreationException
    {
        m_tmpDir = createTempDir();
        m_indexLock = new IndexLock(m_tmpDir, TEST_SUFFIX);
        m_tmpLockFile = new File(m_tmpDir, LOCK_FILE_NAME + "."
                + Integer.toHexString(TEST_SUFFIX));
    }

    @After
    public void after()
    {
        if (m_indexLock != null)
        {
            m_indexLock.release();
        }

        try
        {
            FileUtils.deleteDirectory(m_tmpDir);
        } catch (IOException e)
        {
            //tried to delete... ignore error
        }
    }

    @Test
    public void test_lockFileIsNotDeletable() throws IOException, IndexCreationException
    {
        /*System.out.println(m_tmpLockFile.delete());*/
        //assertFalse(m_tmpLockFile.delete());
        /* file lock is system specific wont work on Mac or Linux
         * This file-locking API is intended to map directly to
         * the native locking facility of the underlying operating system.
         */
    }

    @Test
    public void test_lockFileIsDeletedOnRelease()
    {
        m_indexLock.release();
        assertFalse(m_tmpLockFile.exists());
    }

    @Test
    public void test_onlyOneLockAllowed() throws IndexCreationException
    {
        //test in second thread to confirm change of behavior in JDK 6
        new Runnable() {
            public void run()
            {
                try
                {
                    new IndexLock(m_tmpDir, TEST_SUFFIX);
                    fail("Should have failed to get second lock.");
                } catch (IndexCreationException e)
                {
                    //expected path...
                }
            }
        }.run();
    }

    @Test
    public void test_failedLockDoesNotRetainChannel()
    {
        //test initialize first lock

        //try and get the same build lock again
        try
        {
            new IndexLock(m_tmpDir, TEST_SUFFIX);
            fail("Should have failed to get second lock.");
        } catch (IndexCreationException e)
        {
            //expected path
        }

        //release the original lock
        m_indexLock.release();

        //confirm file deleted (which won't happen if the channel used in the
        //failed lock is left unclosed.
        assertFalse(m_tmpLockFile.exists());
    }

    private File createTempDir() throws IOException
    {
        File tmpFile = File.createTempFile(FILE_NAME, "tmp");

        if (!tmpFile.delete())
        {
            fail("Failed creating temp directory");
        }

        if (!tmpFile.mkdir())
        {
            fail("Failed creating temp directory");
        }
        return tmpFile;
    }
}
