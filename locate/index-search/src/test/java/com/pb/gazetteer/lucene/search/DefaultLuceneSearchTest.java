package com.pb.gazetteer.lucene.search;

import com.pb.gazetteer.search.DefaultLuceneSearch;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/6/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultLuceneSearchTest extends TestCase {
    private static DefaultLuceneSearch dls = null;

    @Before
    public void setUp() {
        dls = new DefaultLuceneSearch(" default QuerY and whitspaces    ");
    }

    @Test
    public void checkInstanceCreation() {
        assertNotNull(dls);
        dls.getProcessedQuery();
    }

    @Test
    public void testQuerySeparatedByAND() {
        String query = "King's are great in. the streets of American Street's.";
        dls.setOriginalQuery(query);
        assertEquals("(king's OR kings) AND are " +
                "AND great AND in AND the " +
                "AND (streets OR street's) AND of AND " +
                "american AND (street's OR streets)", dls.getProcessedQuery());

    }

    @Test
    public void testMultipleWhiteSpaces() {
        String query = "King's       saurabh.";
        dls.setOriginalQuery(query);
        assertEquals("(king's OR kings) AND saurabh", dls.getProcessedQuery());

    }

    @Test
    public void testGetParsedQueryPostcode()
    {
        String query = "eclv 2nt";
        dls.setOriginalQuery(query);
        String expResult = "eclv AND 2nt";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    @Test
    public void testGetParsedQueryPostcodeJoined()
    {
        String query = "ec1v2nt";
        dls.setOriginalQuery(query);
        String expResult = "(ec1v2nt) OR (ec1v AND 2nt)";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    @Test
    public void testGetParsedQueryPartialPostcode()
    {
        String query = "ec1v 2n";
        dls.setOriginalQuery(query);
        String expResult = "(ec1v AND 2n) OR (ec1v AND 2n)";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    @Test
    public void testGetParsedQueryPostcodeJoinedPartialInwardCode1()
    {
        String query = "ec1v2n";
        dls.setOriginalQuery(query);
        String expResult = "(ec1v2n) OR (ec1v AND 2n)";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testGetParsedQueryPostcodePartialInwardCode2()
    {
        String query = "ec1v 2";
        dls.setOriginalQuery(query);
        String expResult = "(ec1v AND 2) OR (ec1v AND 2)";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testGetParsedQueryPostcodeJoinedPartialInwardCode2()
    {
        String query = "ec1v2";
        dls.setOriginalQuery(query);
        String expResult = "(ec1v2) OR (ec1v AND 2)";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testGetParsedQueryNumber()
    {
        String query = "12";
        dls.setOriginalQuery(query);
        String expResult = "12";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testGetParsedQueryReverseApostrophe()
    {
        String query = "What the! King's ";
        dls.setOriginalQuery(query);
        String expResult = "what AND the\\! AND (king's OR kings)";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testGetParsedQuerySpacePadded()
    {
        String query = "    186     City    Road     ";
        dls.setOriginalQuery(query);
        String expResult =
                "186 AND city AND road";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testGetParsedQueryHyphen()
    {
        String query = "Neals-Yard";
        dls.setOriginalQuery(query);
        String expResult = "neals\\-yard";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testGetParsedQueryUsingStopWord()
    {
        String query = "50 The Avenue";
        dls.setOriginalQuery(query);
        String expResult =
                "50 AND the AND avenue";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testQueryParserOperatorAndShouldBeLowerCase()
    {
        String query = "King AND";
        dls.setOriginalQuery(query);
        String expResult = "king AND and";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testQueryParserOperatorOrShouldBeLowerCase()
    {
        String query = "King OR";
        dls.setOriginalQuery(query);
        String expResult = "king AND or";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testQueryParserHyphenBetweenDigits()
    {
        String query = "23-25 Leather Lane";
        dls.setOriginalQuery(query);
        String expResult =
                "23 AND 25 AND leather AND lane";
        assertEquals(expResult, dls.getProcessedQuery());
    }


    public void testQueryFormedForWildCard1()
    {
        String query = "Cam*";
        dls.setOriginalQuery(query);
        String expResult =
                "cam*";
        assertEquals(expResult, dls.getProcessedQuery());
    }

    public void testQueryFormedForWildCard2()
    {
        String query = "Cam?en";
        dls.setOriginalQuery(query);
        String expResult =
                "cam?en";
        assertEquals(expResult, dls.getProcessedQuery());
    }


    public void testQueryFormedForSpecialCharacters()
    {
        String query = "44~a\\pp3811";
        dls.setOriginalQuery(query);
        String expResult =
                "(44\\~a\\\\pp3811) OR (pp38 AND 1 AND 44\\~a\\\\)";
        assertEquals(expResult, dls.getProcessedQuery());
    }


}
