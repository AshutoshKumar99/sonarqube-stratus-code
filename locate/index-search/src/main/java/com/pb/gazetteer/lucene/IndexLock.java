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
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * This class 'locks' a given file with a FileLock, as a mechanism for
 * preventing index cleanup during index creation.
 * <p/>
 * JDK 6 changed the behavior of FileLock within the VM.  Exclusive FileLocks
 * in different threads (on different channels instances) within the same VM
 * should now block each other.  There is a system property that can set the
 * VM to operate in a backward compatible fashion which could cause problems
 * for this service.
 */
public class IndexLock
{
    public final static Logger logger = LogManager.getLogger(IndexLock.class);

    private static final String INDEX_LOCK_FILE_NAME_BASE = "index.lock";
    private final File m_file;
    private FileChannel m_channel;
    private FileLock m_lock;

    public IndexLock(File baseDir, int suffix) throws IndexCreationException
    {
        m_file = getIndexLockFile(baseDir, suffix);

        try
        {
            m_channel = new RandomAccessFile(m_file, "rw").getChannel();
            m_lock = m_channel.tryLock();
        } catch (OverlappingFileLockException e)
        {
            IOUtils.closeQuietly(m_channel);
            throw new IndexCreationException("Unable to lock file.", e);
        } catch (IOException e)
        {
            logger.error("I/O Exception Occurred",e);
        }
    }

    /**
     * Releases the lock, and deletes the lock file.
     */
    public void release()
    {
        try
        {
            m_lock.release();
        } catch (IOException e)
        {
            //tried our best to release, ignore error...
            logger.error(" Error in releasing lock: " +  e);
        }

        IOUtils.closeQuietly(m_channel);
        FileUtils.deleteQuietly(m_file);
    }

    private File getIndexLockFile(File baseDir, int suffix)
    {
        return new File(baseDir, INDEX_LOCK_FILE_NAME_BASE + "."
                + Integer.toHexString(suffix));
    }
}
