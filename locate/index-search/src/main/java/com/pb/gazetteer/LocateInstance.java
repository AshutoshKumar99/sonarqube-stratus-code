package com.pb.gazetteer;

import com.pb.gazetteer.lucene.IndexDir;
import com.pb.gazetteer.lucene.LuceneIndexCleaner;
import com.pb.gazetteer.webservice.LocateException;

import java.util.List;

/**
 * A LocateInstance is a specific realisation of a LocateEngine.
 */
public class LocateInstance {
    private static final String INSTANCE_NOT_FOUND = "Gazetteer is not defined.";

    private String name;
    private LocateEngine engine;
    private IndexSearchFactory indexFactory;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEngine(LocateEngine engine) {
        this.engine = engine;
    }

    public LocateEngine getEngine() {
        return engine;
    }

    public void setIndexFactory(IndexSearchFactory indexFactory) {
        this.indexFactory = indexFactory;
    }

    public IndexSearchFactory getIndexFactory() {
        return indexFactory;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LocateInstance)) {
            return false;
        }
        LocateInstance locateInstance = (LocateInstance) obj;
        return name.equals(locateInstance.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public List<Address> search(SearchParameters searchParameters) throws LocateException {
        IndexSearchFactory indexSearchFactory = getIndexFactory();
        if (indexSearchFactory == null) {
            throw new LocateException(INSTANCE_NOT_FOUND);
        }

        IndexSearch index = indexSearchFactory.getIndex(this);

        return index.search(
                searchParameters.getSearchString(),
                searchParameters.getSearchLogic(),
                searchParameters.getMaxRecords());
    }

    public IndexDir getLatestReadyIndex(LuceneIndexCleaner cleaner) {
        return null;
    }


}