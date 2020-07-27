package com.pb.gazetteer.lucene.search;

import com.pb.gazetteer.search.QueryUtils;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/6/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryUtilsTest extends TestCase {

    @Test
    public void testWhiteSpaceReplacement() {
        String query = "Street White Road";
        query = QueryUtils.checkAndReplaceWhiteSpacesWithAnd(query);
        assert (query.length() > 0);
        assertEquals(query, "Street AND White AND Road");
    }

    @Test
    public void testNullWhiteSpaceReplacementNoException() {
        String query = "";
        query = QueryUtils.checkAndReplaceWhiteSpacesWithAnd(query);
        assertNotNull(query);
    }

    @Test
    public void testApostropheReplacementFunction() {
        String query = "Street\'s White Road";
        query = QueryUtils.checkAndApostrophe(query);

        assert (query.length() > 0);
        assertEquals("Streets White Road", query);
    }


    @Test
    public void testNullApostropheReplacementFunction() {
        String query = null;
        query = QueryUtils.checkAndApostrophe(query);
        assertNull(query);
    }

    @Test
    public void testApostropheAndOrFunction() {
        String query = "Saurabh Street's available streets i'll";
        query = QueryUtils.replaceApostropheWordReplacement(query);

        assert (query.length() > 0);
        assertEquals("Saurabh (Street's OR Streets) available (streets OR street's) i'll", query);
    }

    @Test
    public void testQueryCreation1() {
        String query = "Search the Street's for me and find me streets that have k'ill in it.";
        query = QueryUtils.processUserQuery(query);
        assertNotNull(query);

        assertEquals("search AND the AND (street's OR " +
                "streets) AND for AND me AND and AND find AND" +
                " me AND (streets OR street's) AND that AND have AND " +
                "k'ill AND in AND it", query);
    }

    @Test
    public void testQueryCreation2()
    {
        String query = "king's road TWLL-YN-Y-WAL";
        query = QueryUtils.processUserQuery(query);
        assertNotNull(query);

        assertEquals("(king's OR kings) AND road AND twll\\-yn\\-y\\-wal", query);
    }

    @Test
    public void testQueryCreation3()
    {
        String query = "(king's)";
        query = QueryUtils.processUserQuery(query);
        assertNotNull(query);

        assertEquals("\\(king's\\)", query);
    }


    @Test
         public void testQueryCreation4() {
        String query = "Cam*";
        query = QueryUtils.processUserQuery(query);
        assertNotNull(query);

        assertEquals("cam*", query);
    }
    @Test
    public void testQueryCreation5() {
        String query = "Cam*den";
        query = QueryUtils.processUserQuery(query);
        assertNotNull(query);

        assertEquals("cam*den", query);
    }


    @Test
    public void testQueryCreation6() {
        String query = "Cam?en";
        query = QueryUtils.processUserQuery(query);
        assertNotNull(query);

        assertEquals("cam?en", query);
    }

    @Test
    public void testNoEscapeSpecialCharacters()
    {
        QueryUtils instance = new QueryUtils();
        String expResult =
                "NoSpecialCharacter";
        String actual = instance.escapeSpecialCharacters("NoSpecialCharacter");
        assertEquals(expResult, actual);
    }

    @Test
    public void testEscapeSpecialCharacters()
    {
        QueryUtils instance = new QueryUtils();
        String expResult =
                "Many\\^?\\:\\&Speci\\-\\+alCharacters";
        String actual = instance.escapeSpecialCharacters("Many^?:&Speci-+alCharacters");
        assertEquals(expResult, actual);
    }


}
