package com.pb.gazetteer.search;


/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/6/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public enum SearchLogic {
    DEFAULT_LOGIC(0),
    LUCENE_IMPLICIT(1),
    OTHERS(100);

    int type;

    private SearchLogic(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static SearchLogic fromValue(int val) {
        switch (val) {
            case 0:
                return DEFAULT_LOGIC;
            case 1:
            default:
                return OTHERS;
        }
    }
}
