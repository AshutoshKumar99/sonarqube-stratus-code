package com.pb.gazetteer.lucene;

import java.io.File;
import java.util.SortedSet;

import com.pb.gazetteer.LocateInstance;
import com.pb.gazetteer.search.SearchLogic;
import com.pb.gazetteer.webservice.LocateException;

/**
 * An instance of a Lucene search engine. a LuceneInstance manages remote and 
 * local index directories to allow for index hot swapping.
 */
public class LuceneInstance extends LocateInstance
{
    private String srs;
    
    private File baseDir;

    private SearchLogic searchLogic;

    public SearchLogic getSearchLogic()
    {
        return searchLogic;
    }

    public void setSearchLogic(SearchLogic searchLogic)
    {
        this.searchLogic = searchLogic;
    }

    public void setLocalIndexDir(File localDir)
    {
        this.baseDir = localDir;
    }

    public String getSrs()
    {
        return srs;
    }

    public void setSrs(String srs)
    {
        this.srs = srs;
    }

    public IndexDir createNewIndexDir() throws LocateException
    {
        return IndexDir.createNewIndexDir(baseDir);
    }

    /**
     * Gets the latest index that should be read for the index.
     * @param cleaner
     * @return
     */
    public IndexDir getLatestReadyIndex(LuceneIndexCleaner cleaner)
    {
        return IndexDir.getLatestReadyIndex(baseDir, cleaner);
    }
}
