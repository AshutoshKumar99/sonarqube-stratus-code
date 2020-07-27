package com.pb.gazetteer.lucene;

import com.pb.gazetteer.Address;
import com.pb.gazetteer.IndexSearchException;
import com.pb.gazetteer.resources.TestDirectory;
import com.pb.gazetteer.search.SearchLogic;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * No mocks are used as some of the classes are final and cannot be mocked.
 */

public class LuceneIndexSearchTest {

    private static LuceneIndexSearch index;

    @BeforeClass
    public static void setUp() throws Exception {
        index = new LuceneIndexSearch();
        index.setDirectory(TestDirectory.searchDirectory());
        index.setSrs("epsg:1234");
        index.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
    }

    /*
    * Empty or null queries
    */

    @Test
    public void testSearchEmptyQuery() throws IndexSearchException {
        try {
            List<Address> addresses = index.search("");
            fail("No ParseException thrown");
        } catch (IndexSearchException px) {
            //expected
        }
    }

    @Test
    public void testCommaQuery() throws IndexSearchException {
        try {
            List<Address> addresses = index.search("Bramshurst,");
            displayAddresses(addresses);
            assert(addresses.size() == 3);
        } catch (IndexSearchException px) {
            fail();
        }
    }

    @Test
    public void testCommaQuery1() throws IndexSearchException {
        try {
            List<Address> addresses = index.search("Bramshurst");
            assert(addresses.size() == 3);
        } catch (IndexSearchException px) {
            fail();
        }
    }

    @Test
    public void testSearchNullQuery() throws IndexSearchException {
        try {
            index.search(null);
            fail("No ParseException thrown");
        } catch (IndexSearchException px) {
            //expected
        }
    }

    /*
    * Regular searches
    */

    @Test
    public void testSearchForNonExistentItem() throws Exception {
        List<Address> addresses = index.search("aardvark");
        assertEquals(0, addresses.size());
    }

    @Test
    public void testSearchForValidAddress01() throws Exception {
        List<Address> addresses = index.search("186 City Road");
        assertEquals(1, addresses.size());
        String expectedAddressId = "1";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchForValidAddress02() throws Exception {
        List<Address> addresses = index.search("69 Wilson Street");
        assertEquals(1, addresses.size());
        String expectedAddressId = "2";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchWithMultipleResults() throws Exception {
        List<Address> addresses = index.search("Camden Road");
        assertEquals(4, addresses.size());
        String expectedAddressIdNo4 = "4";
        String expectedAddressIdNo5 = "5";
        String expectedAddressIdNo6 = "6";
        String expectedAddressIdNo7 = "7";
        assertEquals(expectedAddressIdNo4, addresses.get(0).getId());
        assertEquals(expectedAddressIdNo5, addresses.get(1).getId());
        assertEquals(expectedAddressIdNo6, addresses.get(2).getId());
        assertEquals(expectedAddressIdNo7, addresses.get(3).getId());
    }

    @Test
    public void testSearchIsCaseInsensitive() throws Exception {
        List<Address> addresses = index.search("CITY road");
        assertEquals(1, addresses.size());
        String expectedAddressId = "1";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchFavoursAddresses() throws Exception {
        List<Address> addresses = index.search("12 camden road St Pancras");
        assertEquals(1, addresses.size());
        String expectedAddressId = "5";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchResultsAreLimitedToMaximum() throws Exception {
        List<Address> addresses = index.search("12", 2);
        assertEquals(2, addresses.size());
    }

    /*
    * Stop words searches
    */

    @Test
    public void testSearchFavoursAddressWithStopWord() throws Exception {
        List<Address> addresses = index.search("50 The Avenue");
        assertEquals(1, addresses.size());
        String expectedAddressId = "8";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    private void displayAddresses(List<Address> addresses)
    {
        for(Address add:addresses){
            System.out.println("Address: " + add.getAddress() + " ID: " + add.getId() + " Score: " + add.getScore());
        }
    }

    /*
    * Apostrophe
    */
    @Test
    public void testSearchSpecialCharactersEscaped() throws Exception {
        String query = "king's";
        List<Address> addresses = index.search(query);
        displayAddresses(addresses);
        assertEquals(1, addresses.size());
        String expectedAddressId = "9";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    /*
     * wild card tests
     */
    @Test
    public void testSearchWithAsterik() {
        List<Address> addresses = index.search("Camd*");
        assert (addresses.size() == 5);

    }


    @Test
    public void testSearchWithQuestionMarkLocation() {
        List<Address> addresses = index.search("Cam?en");
        assert (addresses.size() == 5);

    }

    @Test
    public void testSearchWithInvalidQuestionMarkLocation() {
        List<Address> addresses = index.search("Camden?");
        assert (addresses.size() == 0);

    }

    @Test
    public void testSearchWithWildCardTwo() {

        List<Address> addresses = index.search("Cambridge*");
        assert (addresses.size() == 4);
    }

    /*
     * special character test
     */

    @Test
    public void testSearchWithOnlySpecialCharactersAbsent() throws Exception {

        List<Address> addresses = index.search("!@%&^%*&%^(&!#!#$@&*%*&@$!#!#");
        assert (addresses.size() == 0);

    }

    @Test
    public void testSearchWithOnlySpecialCharactersPresent() throws Exception {

        List<Address> addresses = index.search("笑手机 Ww\\7 på også læse");
        displayAddresses(addresses);
        assert (addresses.size() == 1);

    }
    /*
     *  DPTLI customer test cases
     */
    @Test
    public void testSearchWithSpecialCharacters() throws Exception {

        List<Address> addresses = index.search("50\\~a\\pp3811");
        assert (addresses.size() == 0);

    }

    @Test
    public void testSearchWithDPTLISpecialCharacters() throws Exception {

        List<Address> addresses = index.search("44\\~a\\pp3811");
        assert (addresses.size() == 0);

    }

    @Test
    public void testSearchHyphenInQuery() throws Exception {
        String query = "TWLL-YN-Y-WAL";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "9";
        assertEquals(expectedAddressId, addresses.get(0).getId());
        assertEquals("12 King's road TWLL-YN-Y-WAL London. NW1 9DP", addresses.get(0).getAddress());
    }

    @Test
    public void testSearchHyphenAndApostropheInQuery() throws Exception {
        String query = "king's road TWLL-YN-Y-WAL";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "9";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchBracketsInQuery() throws Exception {
        String query = "Stratus Noida (1)";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "30";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchNoApostropheInQuery() throws Exception {
        //Expecting to return the same result as with Apostrophe
        String query = "kings road";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "9";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchWithAmpersand() throws Exception {
        String query = "Stratus & Connect";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "20";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    /*
     * Extended characters search strings
     */

   /* @Test
    public void testSearchWithNonEnglishCharacters() throws Exception {

        String query = new String("FriedensstraÃŸe RÃ¶derland".getBytes("UTF-8"));
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "19";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }*/

    @Test
    public void testCircularBrace() {
        String query = "(";
        List<Address> addresses = index.search(query);
        assert (addresses.size() == 0) ;
    }
    /*
     * Sort order tests for fuzzy search
     */

    @Test
    public void testSearchResultsSortOrder() throws Exception {
        /*
         * The purpose is for the exact match to be the first result returned
         * and the fuzzy search results thereafter.
         */
        String query = "Cambridgeshire Road";
        List<Address> addresses = index.search(query);
        assertEquals(3, addresses.size());
        String expectedAddressIdNo10 = "10";
        String expectedAddressIdNo11 = "11";
        String expectedAddressIdNo12 = "12";
        assertEquals(expectedAddressIdNo10, addresses.get(0).getId());
        assertEquals(expectedAddressIdNo11, addresses.get(1).getId());
        assertEquals(expectedAddressIdNo12, addresses.get(2).getId());
    }

    /*
    *  Query parser specific words checks
    *  The purpose is to make sure queries with Capital AND OR does not give
    *  errors as these are operators used by query parser.
    */

    @Test
    public void testSearchWithQueryParserOperatorAnd() throws Exception {
        String query = "Victoria AND";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "13";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchWithQueryParserOperatorOr() throws Exception {
        String query = "Exmouth or Mortimer";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "29";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }


    /*
    * Post code checks
    */

    @Test
    public void testSearchPostCodeFormatA9_9AA() {
        String query = "M1 1AA";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "22";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatA99_9AA() {
        String query = "M60 1NW";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "23";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatA9A_9AA() {
        String query = "W1A 1HQ";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatAA9_9AA() {
        String query = "CR2 6XH";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "25";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatAA99_9AA() {
        String query = "DN55 1PT";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "26";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatAA9A_9AA() {
        String query = "EC1A 1BB";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "28";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    /*
    * Complete postcodes without spaces
    */

    public void testSearchPostCodeFormatA99AA() {
        String query = "M11AA";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "22";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatA999AA() {
        String query = "M601NW";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "23";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatA9A9AA() {
        String query = "W1A1HQ";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatAA99AA() {
        String query = "CR26XH";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "25";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatAA999AA() {
        String query = "DN551PT";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "26";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchPostCodeFormatAA9A9AA() {
        String query = "EC1*";         //EC1A 1BC
        List<Address> addresses = index.search(query);
        assertEquals(6, addresses.size());
   }

    /*
     * Danish Post Codes
     */

    @Test
    public void testDanishFullPostCode() throws Exception {
        String query = "E11N*";
        List<Address> addresses = index.search(query);
        displayAddresses(addresses);
        assertEquals(2, addresses.size());

    }

    @Test
    public void testDanishPostCodeWildCard() throws Exception {
        String query = "E11N0870";
        List<Address> addresses = index.search(query);
        displayAddresses(addresses);
        assertEquals(1, addresses.size());

    }

    @Test
    public void testSearchOutwardPostCodeFormatA9() {
        String query = "M1";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "22";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchOutwardPostCodeFormatA99() {
        String query = "M60";
        List<Address> addresses = index.search(query);
        assertEquals(2, addresses.size());
        String expectedAddressId = "24";
        assertEquals(expectedAddressId, addresses.get(0).getId());
        expectedAddressId = "23";
        assertEquals(expectedAddressId, addresses.get(1).getId());
    }

    @Test
    public void testSearchOutwardPostCodeFormatA9A() {
        String query = "W1A";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchOutwardPostCodeFormatAA9() {
        String query = "CR2";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "25";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchOutwardPostCodeFormatAA99() {
        String query = "DN55";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "26";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testSearchOutwardPostCodeFormatAA9A() {
        String query = "EC1A";
        List<Address> addresses = index.search(query);
        assertEquals(2, addresses.size());

    }

    /*
     * Tests with text and post codes
     */

    @Test
    public void testStringWithStreetNameOutwardPostCode() {
        String query = "Lancashire M1";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "22";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testStringFullPostCode() {
        String query = "W1A 1HQ";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testStringFullPostCodeMissingFullInfo() {
        String query = "W1A 1H";
        List<Address> addresses = index.search(query);
        assertEquals(0, addresses.size());

    }

    @Test
    public void testStringWithStreetNameFullPostCode() {
        String query = "70 Mortimer street W1A 1HQ";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }


    @Test
    public void testStringWithPlaceAndHalfPostalCode() {
        String query = "Cambridgeshire NW1 ";
        List<Address> addresses = index.search(query);
        assertEquals(4, addresses.size());

    }


    @Test
    public void testStringWithFuzzyPLaceAndHalfPostalCode() {
        String query = "Cambridg* NW1";
        List<Address> addresses = index.search(query);
        assertEquals(4, addresses.size());

    }

    @Test
    public void testWildCardQuestionMarkSearch() {
        String query = "Cam*";
        List<Address> addresses = index.search(query);
        assertEquals(9, addresses.size());
    }


    @Test
    public void testWildCardAsterikSearch() {
        String query = "Cam?en";
        List<Address> addresses = index.search(query);
        assertEquals(5, addresses.size());
    }




    @Test
    public void testStringWithStreetNameOutwardPostCodeAndSomeMore() {
        String query = "70 Mortimer street W1A London";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMore() {
        String query = "70 Mortimer street W1A 1HQ London";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testStringWithStreetNameFullPostCodeNoSpace() {
        String query = "70 Mortimer street W1A1HQ";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreNoSpace() {
        String query = "70 Mortimer street W1A1HQ London";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testStringWithStreetNamePartialPostCode() {
        String query = "70 Mortimer street W1A1HQ London";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "27";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testStringWithFancyChars() {
        String query = "25a~b\\pp3850";
        List<Address> addresses = index.search(query);
        displayAddresses(addresses);
        assertEquals(1, addresses.size());
        String expectedAddressId = "41";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    @Test
    public void testStringWithPartialPostCode() {
        String query = "W1A1H";
        List<Address> addresses = index.search(query);
        assertEquals(0, addresses.size());
    }

    @Test
    public void testHyphenWithStrings() {
        String query = "Neals-Yard";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
        String expectedAddressId = "32";
        assertEquals(expectedAddressId, addresses.get(0).getId());
    }

    /*
     *  Test cases for Combinations of postalCode with non postal tokens on either side.
     */


    @Test
    public void testPostalCodeFollowedByNonPostalTokens() {
        String query = "WC1H 8EQ good street";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
    }

    @Test
    public void testNonPostalTokensFollowedByPostalCode() {
        String query = "8/6 WC1H 8EQ";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
    }

    @Test
    public void testNonPostalCodeEitherSideOnPostalCode() {
        String query = "3/3 WC1H 8EQ good street";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
    }

    @Test
    public void testNoSpacePostalCodeFollowedByNonPostalCode() {
        String query = "WC1H8EQ good street";
        List<Address> addresses = index.search(query);
        assertEquals(2, addresses.size());
    }

    @Test
    public void testNonPostalFollowedByNoSpacePostalCode() {
        String query = "8/56 WC1H8EQ";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
    }

    @Test
    public void testNonPostalEitherSideNoSpacePostalCode() {
        String query = "8/56 WC1H8EQ good street";
        List<Address> addresses = index.search(query);
        assertEquals(1, addresses.size());
    }

    @Test
    public void testWildCardAndPostalCodeToken() {
        String query = "mo* wc1h 8eq";
        List<Address> addresses = index.search(query);
        displayAddresses(addresses);
        assertEquals(1, addresses.size());
    }

    /*
     * sequence returned for addresses is same as in csv.
     */
    @Test
    public void testForSequenceAsInCsv(){
        String query = "Leather Lane";
        List<Address> addresses = index.search(query);
        displayAddresses(addresses);
        assertEquals(5, addresses.size());
        assertEquals("14",addresses.get(0).getId());
        assertEquals("15",addresses.get(1).getId());
        assertEquals("16",addresses.get(2).getId());
        assertEquals("17",addresses.get(3).getId());
    }

    @Test
    public void testForSameScoreAsInCsv(){
        String query = "Leather Lane";
        List<Address> addresses = index.search(query);
        assertEquals(5, addresses.size());
        displayAddresses(addresses);
        assertEquals(2.1236846,addresses.get(0).getScore(),7);
        assertEquals(2.1236846,addresses.get(1).getScore(),7);
        assertEquals(2.1236846,addresses.get(2).getScore(),7);
        assertEquals(2.1236846,addresses.get(3).getScore(),7);

    }


    @Test
    public void testForHyphen(){
        String query = "3 8";
        List<Address> addresses = index.search(query);
        displayAddresses(addresses);
        assert(addresses.size() == 2);
        assertEquals(addresses.get(0).getId(), "56");
        assertEquals(addresses.get(1).getId(), "57");
    }


    @Test
    public void testForwardSlash()
    {
        String query = "Ww\\ps718774";
        List<Address> addresses = index.search(query);
        displayAddresses(addresses);
        assert(addresses.size() == 1);
        assertEquals(addresses.get(0).getId(), "48");
    }

    /**
     * Range tests
     * @throws Exception
     */
    @Test
    public void testSearchWithAddressRange() throws Exception {
        String query = "23-25 Leather Lane";
        List<Address> addresses = index.search(query);
        displayAddresses(addresses);
        assertEquals(2, addresses.size());
        String expectedAddressIdNo17 = "17";
        String expectedAddressIdNo59 = "59";
        assertEquals(expectedAddressIdNo17, addresses.get(0).getId());
        assertEquals(expectedAddressIdNo59, addresses.get(1).getId());

    }

    @Test
    public void testSearchWithAddressRangeWithHyphen() throws Exception {
        String query = "23-25";
        List<Address> addresses = index.search(query);

        displayAddresses(addresses);
        assertEquals(2, addresses.size());
        String expectedAddressIdNo17 = "17";
        String expectedAddressIdNo59 = "59";
        assertEquals(expectedAddressIdNo17, addresses.get(0).getId());
        assertEquals(expectedAddressIdNo59, addresses.get(1).getId());


    }



    @Test
    public void testSearchWithAddressRangeWithSpace() throws Exception {
        String query = "23 25";
        List<Address> addresses = index.search(query);

        displayAddresses(addresses);
        assertEquals(2, addresses.size());
        String expectedAddressIdNo17 = "17";
        String expectedAddressIdNo59 = "59";
        assertEquals(expectedAddressIdNo17, addresses.get(0).getId());
        assertEquals(expectedAddressIdNo59, addresses.get(1).getId());


    }

    @Test
    public void testSearchWithAddressRangeWithSpaceAndAddress() throws Exception {
        String query = "23 25 London";
        List<Address> addresses = index.search(query);

        displayAddresses(addresses);
        assertEquals(2, addresses.size());
        String expectedAddressIdNo17 = "17";
        String expectedAddressIdNo59 = "59";
        assertEquals(expectedAddressIdNo17, addresses.get(0).getId());
        assertEquals(expectedAddressIdNo59, addresses.get(1).getId());


    }


    @Test
    public void searchInvalidWildCardInStart() throws Exception {
        String query = "*BlahBlahBlah";
        List<Address> addresses =null;
        try{
        addresses = index.search(query);

        }catch(Exception x)
        {
         assertNotNull(x);
        } finally
        {
            assertNull(addresses);
        }
    }


    @Test
    public void searchInvalidSingleWildCard() throws Exception {
        String query = "*";
        List<Address> addresses =null;
        try{
            addresses = index.search(query);

        }catch(Exception x)
        {
            assertNotNull(x);
        } finally
        {
            assertNull(addresses);
        }
    }

    @Test
    public void testSearchWithASingleNumber() throws Exception {
        String query = "23";
        List<Address> addresses = index.search(query);

        displayAddresses(addresses);
        assertEquals(3, addresses.size());
        String expectedAddressIdNo14 = "14";
        String expectedAddressIdNo17 = "17";
        String expectedAddressIdNo59 = "59";
        assertEquals(expectedAddressIdNo14, addresses.get(0).getId());
        assertEquals(expectedAddressIdNo17, addresses.get(1).getId());
        assertEquals(expectedAddressIdNo59, addresses.get(2).getId());

    }

}
