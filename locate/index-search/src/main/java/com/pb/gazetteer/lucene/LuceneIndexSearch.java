/**
 * Changed the encoding for the file.
 **/
package com.pb.gazetteer.lucene;

import com.pb.custom.lucene.CustomAnalyzer;
import com.pb.gazetteer.Address;
import com.pb.gazetteer.IndexSearch;
import com.pb.gazetteer.IndexSearchException;
import com.pb.gazetteer.search.DefaultLuceneSearch;
import com.pb.gazetteer.search.SearchLogic;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.pb.gazetteer.lucene.LuceneIndexerConstants.*;

/**
 * An IndexSearch implementation operating on a Lucene index. The Lucene index
 * being used can be set with {@link #setDirectory(Directory)}. The index
 * is expected to contain the fields id, x, y, and address.
 */
public class LuceneIndexSearch implements IndexSearch {
    private static final Logger logger = LogManager.getLogger(LuceneIndexSearch.class);

    private static final int DEFAULT_MAX_RECORDS = 1000;

    private Directory directory;

    private String srs;

    /**
     * Introducing a enumeration for search logic.
     */
    private SearchLogic searchLogic;

    public LuceneIndexSearch() {
        directory = NullDirectory.newInstance();
    }

    public List<Address> search(String queryString, SearchLogic searchLogic, int maxRecords)
            throws IndexSearchException {
        try {
            /*
             * Empty string array, which means no STOP WORD such as The, Of and
             * so on will also be indexed.
             */
            Analyzer analyzer = new CustomAnalyzer();
            Directory directory = getDirectory();
            DirectoryReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            //QueryUtils.readIndex(reader);
            /**
             * Using default QueryParser only.
             */
            QueryParser parser = new QueryParser(Version.LUCENE_45, SEARCH_ADDRESS, analyzer);
            Query query = null;
            String preparedQuery = null;

            if (searchLogic.equals(SearchLogic.LUCENE_IMPLICIT)) {
                /**
                 * Stick to the default lucene searching
                 */
                preparedQuery = queryString;

            } else {
                /**
                 * Manipulate the existing query.
                 */
                preparedQuery = prepareQueryString(queryString);

            }

            logger.debug(" Query_Processed: " + preparedQuery);
            query = parser.parse(preparedQuery);

            ScoreDoc[] hits = searcher.search(query, null, maxRecords).scoreDocs;
            List<Address> results = convertToList(searcher, hits, maxRecords);

            reader.close();
            return results;
        } catch (Exception ex) {
            logger.error(ex);
            throw new IndexSearchException("Exception while searching", ex);
        }
    }

    public List<Address> search(String queryString, int maxRecords)
            throws IndexSearchException {
        return search(queryString, SearchLogic.DEFAULT_LOGIC, maxRecords);
    }

    /**
     * Parse the query to modify as per the defined logic.
     *
     * @param query
     * @return
     */
    private String prepareQueryString(String query) throws Exception {
        switch (searchLogic) {
            /**
             * Only one single logic of querying shall exist.
             */
            case DEFAULT_LOGIC: {
                return (new DefaultLuceneSearch(query).getProcessedQuery());
            }

            default: {
                return query;
            }
        }

    }

    public List<Address> search(String queryString)
            throws IndexSearchException {
        return search(queryString, DEFAULT_MAX_RECORDS);
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    private List<Address> convertToList(IndexSearcher searcher, ScoreDoc[] hits, int maxRecords)
            throws IOException {
        int recordCap = getRecordCap(maxRecords, hits.length);
        List<Address> list = new ArrayList<Address>();
        for (int i = 0; i < recordCap; i++) {
            Address address = fromDocument(searcher.doc(hits[i].doc), hits[i].score);
            list.add(address);
        }
        return list;
    }

    private Address fromDocument(Document document, float score) {
        String id = document.get(SEARCH_ID);
        String address = document.get(SEARCH_ADDRESS);
        double x = Double.parseDouble(document.get(SEARCH_X));
        double y = Double.parseDouble(document.get(SEARCH_Y));
        return new Address(id, address, x, y, this.srs, score);
    }

    private int getRecordCap(int maxRecords, int hitsLength) {
        int minimum = Math.min(DEFAULT_MAX_RECORDS, hitsLength);
        if (maxRecords > 0) {
            minimum = Math.min(minimum, maxRecords);
        }
        return minimum;
    }

    public void setSrs(String srs) {
        this.srs = srs;
    }

    public void setSearchLogic(SearchLogic searchLogic) {
        this.searchLogic = searchLogic;
    }
}
