package com.pb.gazetteer.lucene;

import com.pb.gazetteer.IndexAlreadyExistsException;
import com.pb.gazetteer.IndexSearchException;
import com.pb.gazetteer.NoIndexFoundException;
import com.pb.gazetteer.webservice.LocateException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A directory on the file system that represents a Lucene index. In addition
 * to a normal file system directory, this class also maintains whether or not
 * the index directory is ready for consumption. An index directory that is
 * not ready for consumption is typically in the middle of a copy operation.
 *
 * @Author sa021sh
 */

public class IndexDir implements Comparable<IndexDir> {
    private static final Logger logger = LogManager.getLogger(IndexDir.class);
    private static final int MAX_CREATION_TRIES = 5;
    /**
     * Without this file the index would not be read!! :)
     */
    private static final String READY_FILE_NAME_BASE = "index.ready";
    private static final String INDEX_FILE_NAME_BASE = "index.dat";
    private static final Pattern LUCENE_INDEX_PATTERN = Pattern.compile("index\\.dat\\.([0-9abcdef]+)");

    private final File m_baseDir;
    private final int m_suffix;

    /**
     * Default constructor
     *
     * @param baseDir
     * @param suffix
     */
    public IndexDir(File baseDir, int suffix) {
        this.m_baseDir = baseDir;
        this.m_suffix = suffix;
    }

    /**
     * Factory method that allows creation of the index directory!
     *
     * @param baseDir
     * @return
     * @throws LocateException
     */
    public static IndexDir createNewIndexDir(File baseDir) throws LocateException {
        IndexDir indexDir = null;
        for (int i = 0; i < MAX_CREATION_TRIES && indexDir == null; i++) {
            try {
                int nextSuffix = new IndexDirState(baseDir).getHighestSuffix() + 1;
                logger.debug("Index Suffix: " + nextSuffix);
                indexDir = new IndexDir(baseDir, nextSuffix);
                indexDir.create();
            } catch (IndexSearchException e) {
                logger.debug("Failed an attempt at making a new index directory: ", e);
                indexDir = null;
            }
        }

        if (indexDir == null) {
            logger.error("Failed to create index after " + MAX_CREATION_TRIES + " attempts.");
            throw new LocateException("Failed to create index after " + MAX_CREATION_TRIES + " attempts.");
        }

        return indexDir;
    }

    public static IndexDir getLatestReadyIndex(File baseDir, LuceneIndexCleaner cleaner) {
        IndexDirState currentState = new IndexDirState(baseDir);

        IndexDir result = currentState.getLatestReady();
        if (result == null) {
            logger.error(" No index ready file found, throwing exception.");
            throw new NoIndexFoundException();
        }

        //delete and indexes older than the latest ready index
        for (IndexDir indexDir : currentState.getAllIndexes()) {
            if (indexDir.getSuffix() < result.getSuffix()) {
                logger.info(" Deleting the old : " + indexDir.getIndexFile().getName());
                cleaner.delete(indexDir);
            }
        }

        return result;
    }

    public void markReady() {
        File f = getReadyFile();
        try {
            if (!f.createNewFile()) {
                throw new IndexSearchException("Failed to create ready file "
                        + f.getAbsolutePath());
            }
        } catch (IOException iox) {
            throw new IndexSearchException("Failed to create ready file"
                    + f.getAbsolutePath(), iox);
        }
    }


    public void delete() {
        File readyFile = getReadyFile();
        File indexFile = getIndexFile();
        boolean deleteDir = true;

        IndexLock lock = null;
        try {
            lock = acquireIndexLock();

            if (readyFile.exists()) {
                deleteDir = readyFile.delete();
            }

            if (deleteDir) {
                try {
                    FileUtils.deleteDirectory(indexFile);
                } catch (IOException iox) {
                    logger.warn("Failed to delete " + indexFile + ".", iox);
                }
            } else {
                logger.warn("Failed to delete " + readyFile.getAbsolutePath()
                        + ". Neither it nor " + indexFile.getAbsolutePath()
                        + " will be deleted");
            }

        } catch (IndexCreationException e) {
            //failed to acquire lock... fail silently
            logger.error("Index Creation Failed", e);
        } finally {
            if (lock != null) {
                lock.release();
            }
        }
    }

    public Directory getLuceneDir() {
        try {
            return FSDirectory.open(getIndexFile());
        } catch (IOException iox) {
            throw new IndexSearchException(iox);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexDir indexDir = (IndexDir) o;

        if (m_suffix != indexDir.m_suffix) return false;
        if (m_baseDir != null ? !m_baseDir.equals(indexDir.m_baseDir) : indexDir.m_baseDir != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = m_baseDir != null ? m_baseDir.hashCode() : 0;
        result = 31 * result + m_suffix;
        return result;
    }

    public int compareTo(IndexDir o) {
        // descending natural order
        return o.m_suffix - m_suffix;
    }

    public String toString() {
        return IndexDir.class.getName() + "[m_baseDir=" + m_baseDir
                + ",m_suffix=" + m_suffix + "]";
    }

    /**
     * Creates the index directory, for the new index.
     */
    private void create() {
        File f = getIndexFile();

        if (f.exists()) {
            logger.error(" Throwing exception, the file name already present.");
            throw new IndexAlreadyExistsException(
                    "An index file with the name " + f.getAbsolutePath()
                            + " already exists");
        }
        if (!f.mkdir()) {
            throw new IndexSearchException("Failed to create index directory "
                    + f.getAbsolutePath());
        }
    }

    private File getReadyFile() {
        return new File(m_baseDir, getReadyFileName(m_suffix));
    }

    private static String getReadyFileName(int suffix) {
        return READY_FILE_NAME_BASE + "." + Integer.toHexString(suffix);
    }

    /**
     * Get the index file (computed) name
     *
     * @return File
     */
    private File getIndexFile() {
        String fileName = INDEX_FILE_NAME_BASE + "."
                + Integer.toHexString(m_suffix);

        logger.info(" Index file name: " + fileName);
        return new File(m_baseDir, fileName);
    }

    private static int parseSuffix(String filename) {
        Matcher m = LUCENE_INDEX_PATTERN.matcher(filename);
        if (m.matches()) {
            return Integer.parseInt(m.group(1), 16);
        } else {
            return -1;
        }
    }

    int getSuffix() {
        return m_suffix;
    }

    public IndexLock acquireIndexLock() throws IndexCreationException {
        return new IndexLock(m_baseDir, m_suffix);
    }

    /**
     * Private static class.
     */
    private static class IndexDirState {
        private IndexDir m_latestReady;
        private Collection<IndexDir> m_allIndexes = new ArrayList<IndexDir>();
        private int m_highestSuffix = -1;

        private IndexDirState(File baseDir) {
            SortedSet<IndexDir> allIndexDirs = new TreeSet<IndexDir>();
            String[] fileNames = baseDir.list();
            if (fileNames != null) {
                //build a list of all indexDirs
                Set<String> fileNameSet = new HashSet<String>(Arrays.asList(fileNames));
                for (String file : fileNameSet) {
                    int suffix = parseSuffix(file);
                    //retain the highest suffix found
                    m_highestSuffix = Math.max(m_highestSuffix, suffix);
                    if (suffix >= 0) {
                        IndexDir indexDir = new IndexDir(baseDir, suffix);
                        allIndexDirs.add(indexDir);
                    }
                }

                //process list to determine retained state
                for (IndexDir indexDir : allIndexDirs) {
                    //if we haven't found the latest yet, see if this one is
                    if (m_latestReady == null && isReady(indexDir.getSuffix(), fileNameSet)) {
                        m_latestReady = indexDir;
                    } else if (m_latestReady != null) {
                        //we've found the latest ready, record these as older
                        m_allIndexes.add(indexDir);
                    }
                }
            }
        }

        private boolean isReady(int indexSuffix, Set<String> fileNameSet) {
            return fileNameSet.contains(getReadyFileName(indexSuffix));
        }

        public IndexDir getLatestReady() {
            return m_latestReady;
        }

        public Collection<IndexDir> getAllIndexes() {
            return m_allIndexes;
        }

        public int getHighestSuffix() {
            return m_highestSuffix;
        }
    }
}