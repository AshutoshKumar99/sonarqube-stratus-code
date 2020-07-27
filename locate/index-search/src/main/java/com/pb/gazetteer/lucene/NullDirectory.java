package com.pb.gazetteer.lucene;

import java.io.IOException;

import com.pb.custom.lucene.CustomAnalyzer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.pb.gazetteer.IndexSearchException;
import org.apache.lucene.util.Version;

/**
 * A NullDirectory is a convenience implementation giving access to an empty
 * Lucene index.
 */
public class NullDirectory extends RAMDirectory
{
    private static final Logger log = LogManager.getLogger(NullDirectory.class);
    private static final Analyzer analyzer = new CustomAnalyzer();

    public static Directory newInstance()
    {
        try
        {
            /*
             * Empty string array, which means no STOP WORD such as The, Of and
             * so on will also be indexed.
             */
            Directory directory = new RAMDirectory();
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_45, analyzer);
            IndexWriter writer = new IndexWriter(directory, iwc);

            //writer.optimize();
            writer.commit();
            writer.close();
            return directory;
        }
        catch (IOException ex)
        {
            throw new IndexSearchException(ex);
        }
    }
}
