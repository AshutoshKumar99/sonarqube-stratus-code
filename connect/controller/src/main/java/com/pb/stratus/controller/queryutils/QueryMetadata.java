package com.pb.stratus.controller.queryutils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the Metadata of a single query.
 *
 * @author ar009sh
 */
public final class QueryMetadata {

    private static final Logger log = LogManager.getLogger(QueryMetadata.class);

    private String tableName;
    private String queryName;
    private List<CriteriaParams> criteriaParams;

    public QueryMetadata() {
        // avoid nulls
        tableName = "";
        queryName = "";
        criteriaParams = new ArrayList<CriteriaParams>();
    }

    public List<CriteriaParams> getCriteriaParams() {
        return criteriaParams;
    }

    public void setCriteriaParams(List<CriteriaParams> criteriaParams) {
        this.criteriaParams = criteriaParams;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    /**
     * This method prints the xmlData object
     *
     * @return
     */
    public String print() {

        log.debug(XMlConfigConstants.TABLE_NAME + " : " + tableName);
        log.debug(XMlConfigConstants.QUERY_DESC + " : " + queryName);

        for (int i = 0; i < criteriaParams.size(); i++) {
            log.debug(criteriaParams.get(i).toString());
        }
        return "";
    }
}
