package com.pb.gazetteer.lucene.search;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: AR009SH
 * Date: 12/16/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostalCodeTokenizerTests {


    UKQueryTokenizerPostCodeTestHelper postCodeTests;
    UKQueryTokenizerNonPostCodeTestHelper nonPostCodeTests;


    @Before
    public void setUp() throws Exception {
        postCodeTests = new UKQueryTokenizerPostCodeTestHelper();
        nonPostCodeTests = new UKQueryTokenizerNonPostCodeTestHelper();
    }

    @After
    public void tearDown() throws Exception {
        postCodeTests = null;
        nonPostCodeTests = null;
    }

    /*
    * Postcode Tokens Tests
    */
    @Test
    public void testIsTherePostal() {


    }


    // Complete Post code with spaces
    @Test
    public void testCompletePostCodeFormatA9_9AA() {
        postCodeTests.testCompletePostCodeFormatA9_9AA();
    }

    @Test
    public void testCompletePostCodeFormatA99_9AA() {
        postCodeTests.testCompletePostCodeFormatA99_9AA();
    }

    @Test
    public void testCompletePostCodeFormatA9A_9AA() {
        postCodeTests.testCompletePostCodeFormatA9A_9AA();
    }

    @Test
    public void testCompletePostCodeFormatAA9_9AA() {
        postCodeTests.testCompletePostCodeFormatAA9_9AA();
    }

    @Test
    public void testCompletePostCodeFormatAA99_9AA() {
        postCodeTests.testCompletePostCodeFormatAA99_9AA();
    }

    @Test
    public void testCompletePostCodeFormatAA9A_9AA() {
        postCodeTests.testCompletePostCodeFormatAA9A_9AA();
    }

    // Complete postcodes without spaces

    @Test
    public void testCompletePostCodeFormatA99AA() {
        postCodeTests.testCompletePostCodeFormatA99AA();
    }

    @Test
    public void testCompletePostCodeFormatA999AA() {
        postCodeTests.testCompletePostCodeFormatA999AA();
    }

    @Test
    public void testCompletePostCodeFormatA9A9AA() {
        postCodeTests.testCompletePostCodeFormatA9A9AA();
    }

    @Test
    public void testCompletePostCodeFormatAA99AA() {
        postCodeTests.testCompletePostCodeFormatAA99AA();
    }

    @Test
    public void testCompletePostCodeFormatAA999AA() {
        postCodeTests.testCompletePostCodeFormatAA999AA();
    }

    @Test
    public void testCompletePostCodeFormatAA9A9AA() {
        postCodeTests.testCompletePostCodeFormatAA9A9AA();
    }

    // Outward postcode only

    @Test
    public void testOutwardPostCodeFormatA9() {
        postCodeTests.testOutwardPostCodeFormatA9();
    }

    @Test
    public void testOutwardPostCodeFormatA99() {
        postCodeTests.testOutwardPostCodeFormatA99();
    }

    @Test
    public void testOutwardPostCodeFormatA9A() {
        postCodeTests.testOutwardPostCodeFormatA9A();
    }

    @Test
    public void testOutwardPostCodeFormatAA9() {
        postCodeTests.testOutwardPostCodeFormatAA9();
    }

    @Test
    public void testOutwardPostCodeFormatAA99() {
        postCodeTests.testOutwardPostCodeFormatAA99();
    }

    @Test
    public void testOutwardPostCodeFormatAA9A() {
        postCodeTests.testOutwardPostCodeFormatAA9A();
    }

    // Tests with text and post codes

    @Test
    public void testStringWithStreetNameFullPostCode() {
        postCodeTests.testStringWithStreetNameFullPostCode();
    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9A() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9A();
    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA99() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA99();
    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9();

    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9A() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9A();

    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA99() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatA99();
    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9();
    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9ANoSpace() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9ANoSpace();

    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA99NoSpace() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA99NoSpace();

    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9NoSpace() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9NoSpace();
    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9ANoSpace() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9ANoSpace();

    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA99NoSpace() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatA99NoSpace();

    }

    @Test
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9NoSpace() {
        postCodeTests.testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9NoSpace();

    }


    /*
    * Non Postcode Tokens
    */

    // Complete Post code with spaces

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA9_9AA() {
        nonPostCodeTests.testGetNonPostcodeTokensCompletePostCodeFormatA9_9AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA99_9AA() {
        nonPostCodeTests
                .testGetNonPostcodeTokensCompletePostCodeFormatA99_9AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA9A_9AA() {
        nonPostCodeTests
                .testGetNonPostcodeTokensCompletePostCodeFormatA9A_9AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA9_9AA() {
        nonPostCodeTests
                .testGetNonPostcodeTokensCompletePostCodeFormatAA9_9AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA99_9AA() {
        nonPostCodeTests
                .testGetNonPostcodeTokensCompletePostCodeFormatAA99_9AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA9A_9AA() {
        nonPostCodeTests
                .testGetNonPostcodeTokensCompletePostCodeFormatAA9A_9AA();
    }

    // Complete postcodes without spaces

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA99AA() {
        nonPostCodeTests.testGetNonPostcodeTokensCompletePostCodeFormatA99AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA999AA() {
        nonPostCodeTests.testGetNonPostcodeTokensCompletePostCodeFormatA999AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA9A9AA() {
        nonPostCodeTests.testGetNonPostcodeTokensCompletePostCodeFormatA9A9AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA99AA() {
        nonPostCodeTests.testGetNonPostcodeTokensCompletePostCodeFormatAA99AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA999AA() {
        nonPostCodeTests
                .testGetNonPostcodeTokensCompletePostCodeFormatAA999AA();
    }

    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA9A9AA() {
        nonPostCodeTests
                .testGetNonPostcodeTokensCompletePostCodeFormatAA9A9AA();
    }

    // Outward postcode only

    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatA9() {
        nonPostCodeTests.testGetNonPostcodeTokensOutwardPostCodeFormatA9();
    }

    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatA99() {
        nonPostCodeTests.testGetNonPostcodeTokensOutwardPostCodeFormatA99();
    }

    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatA9A() {
        nonPostCodeTests.testGetNonPostcodeTokensOutwardPostCodeFormatA9A();
    }

    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatAA9() {
        nonPostCodeTests.testGetNonPostcodeTokensOutwardPostCodeFormatAA9();
    }

    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatAA99() {
        nonPostCodeTests.testGetNonPostcodeTokensOutwardPostCodeFormatAA99();
    }

    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatAA9A() {
        nonPostCodeTests.testGetNonPostcodeTokensOutwardPostCodeFormatAA9A();
    }

    // Post code with text and post codes

    @Test
    public void testGetNonPostcodeTokensWithStreetNameOutwardPostCode() {
        nonPostCodeTests
                .testGetNonPostcodeTokensWithStreetNameOutwardPostCode();
    }

    @Test
    public void testGetNonPostcodeTokensWithStreetNameFullPostCode() {
        nonPostCodeTests.testGetNonPostcodeTokensWithStreetNameFullPostCode();
    }

    @Test
    public void testGetNonPostcodeTokensWithStreetNameOutwardPostCodeAndSomeMore() {
        nonPostCodeTests
                .testGetNonPostcodeTokensWithStreetNameOutwardPostCodeAndSomeMore();
    }

    @Test
    public void testGetNonPostcodeTokensWithStreetNameFullPostCodeAndSomeMore() {
        nonPostCodeTests
                .testGetNonPostcodeTokensWithStreetNameFullPostCodeAndSomeMore();
    }

    @Test
    public void testGetNonPostcodeTokensWithStreetNameFullPostCodeNoSpace() {
        nonPostCodeTests
                .testGetNonPostcodeTokensWithStreetNameFullPostCodeNoSpace();
    }

    @Test
    public void testGetNonPostcodeTokensWithStreetNameFullPostCodeAndSomeMoreNoSpace() {
        nonPostCodeTests
                .testGetNonPostcodeTokensWithStreetNameFullPostCodeAndSomeMoreNoSpace();
    }


}