package com.pb.gazetteer.search;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/6/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultLuceneSearch implements StratusQueryInterface {

    private final static Logger logger = LogManager.getLogger(DefaultLuceneSearch.class);
    private static final String OR = ") OR (";



    private PostalCodesTokenizer pTokenizer;

    /**
     * The basic elements of the query.
     */
    private String originalQuery;
    private String parsedQuery;

    public DefaultLuceneSearch() {
        this(null);
    }

    public DefaultLuceneSearch(String query) {
        this.setOriginalQuery(query);

    }

    private String preprocessTheInputQuery() {

        if ((this.originalQuery != null) && (!StringUtils.isEmpty(this.originalQuery))) {
            //1. Step to trim the whitespaces at begining and end.
            String newQuery = originalQuery.trim();

            //2. Step replaced the white space with AND.
            newQuery = QueryUtils.processUserQuery(newQuery);

            logger.info(" Query-Processed: " + newQuery);

            return newQuery;
        }
        throw new InvalidQueryException();
    }

    private String putParenthesis(String query) {
        return "(" + query + ")";
    }

    @Override
    public String getParsedQuery() {
        return this.parsedQuery;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public String getNonPostTokens() {
        String partialQuery = this.pTokenizer.removePostCodeTokensFromInputString();

        if (StringUtils.isBlank(partialQuery)||(partialQuery.contentEquals("*"))) {
            return "";
        } else {

            if (StringUtils.isBlank(StringUtils.join(this.pTokenizer.getPostcodeTokens(), QueryUtils.AND))) {
                return partialQuery;
            } else {
                return QueryUtils.AND + partialQuery;
            }

        }
    }


    @Override
    public String getProcessedQuery() {

       if (this.pTokenizer.isTherePostal()) {

            return "(" + this.preprocessTheInputQuery() + OR +
                    StringUtils.join(this.pTokenizer.getPostcodeTokens(), QueryUtils.AND) + this.getNonPostTokens() + ")";
        }

        // This code is actually not required as postal codes regex used here is pretty generic(COWI) and i couldnot find
        // any specific Danish postal code regex on web
       if(this.pTokenizer.isThereDanishPostal())
       {
           return "(" + this.preprocessTheInputQuery() + OR +
                   StringUtils.join(this.pTokenizer.getDanishPostcodeTokens(), QueryUtils.AND) + this.getNonPostTokens() + ")";
       }

        return this.preprocessTheInputQuery();
    }

    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
        this.pTokenizer = new PostalCodesTokenizer(this.originalQuery);
    }
}
