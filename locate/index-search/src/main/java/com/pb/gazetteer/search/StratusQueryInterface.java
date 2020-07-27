package com.pb.gazetteer.search;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/6/13
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StratusQueryInterface {
    /**
     * Returns the modified query information.
     * @return
     */
    public String getParsedQuery() throws Exception;

    /**
     * Get the unmodified query.
     * @return
     */
    public String getOriginalQuery();

    /**
     * The complete search query to be executed.
     */
    public String getProcessedQuery();
}
