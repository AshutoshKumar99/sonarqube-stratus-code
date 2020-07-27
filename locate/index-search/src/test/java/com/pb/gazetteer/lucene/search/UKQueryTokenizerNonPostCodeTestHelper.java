package com.pb.gazetteer.lucene.search;

import com.pb.gazetteer.search.PostalCodesTokenizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UKQueryTokenizerNonPostCodeTestHelper
{
    // Complete Post code with spaces
    @Test
    public void testIsTherePostalforOnePostalToken()
    {
        String text = "aplha beta AA99A gamma";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        boolean check = tokenizer.isTherePostal();
        assertTrue(check);
    }

    /*
     * Non Postcode Tokens
     */

    // Complete Post code with spaces
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA9_9AA()
    {
        String text = "A9 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA99_9AA()
    {
        String text = "A99 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA9A_9AA()
    {
        String text = "A9A 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA9_9AA()
    {
        String text = "AA9 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA99_9AA()
    {
        String text = "AA99 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA9A_9A()
    {
        String text = "AA9A 9A";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA9A_9AA()
    {
        String text = "AA9A 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }

    // Complete postcodes without spaces
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA99AA()
    {
        String text = "A99AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA999AA()
    {
        String text = "A999AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatA9A9AA()
    {
        String text = "A9A9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA99AA()
    {
        String text = "AA99AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA999AA()
    {
        String text = "AA999AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensCompletePostCodeFormatAA9A9AA()
    {
        String text = "AA9A9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }

    // Outward postcode only
    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatA9()
    {
        String text = "A9";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatA99()
    {
        String text = "A99";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatA9A()
    {
        String text = "A9A";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatAA9()
    {
        String text = "AA9";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }
    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatAA99()
    {
        String text = "AA99";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }

    @Test
    public void testGetNonPostcodeTokensOutwardPostCodeFormatAA9A()
    {
        String text = "AA9A";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals(0, nonPostalResult.length());
    }

    // Post code with text and post codes
    @Test
    public void testGetNonPostcodeTokensWithStreetNameOutwardPostCode()
    {
        String text = "test Street AA9A";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals("test AND street AND aa9a", nonPostalResult);
    }

    @Test
    public void testGetNonPostcodeTokensWithStreetNameFullPostCode()
    {
        String text = "test Street AA9A 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals("test AND street", nonPostalResult);
        //assertNonPostCodeStreet(tokens);
    }
    @Test
   public void testGetNonPostcodeTokensWithStreetNameOutwardPostCodeAndSomeMore()
    {
        String text = "test Street AA9A test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals("test AND street AND aa9a AND test AND town", nonPostalResult);
    }
    @Test
    public void testGetNonPostcodeTokensWithStreetNameFullPostCodeAndSomeMore()
    {
        String text = "test Street AA9A 9AA test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals("test AND street AND test AND town", nonPostalResult);
    }
    @Test
    public void testGetNonPostcodeTokensWithStreetNameFullPostCodeNoSpace()
    {
        String text = "test Street AA9A9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals("test AND street", nonPostalResult);
    }
    @Test
    public void testGetNonPostcodeTokensWithStreetNameFullPostCodeAndSomeMoreNoSpace()
    {
        String text = "test Street AA9A9AA test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        String nonPostalResult = tokenizer.removePostCodeTokensFromInputString();
        assertEquals("test AND street AND test AND town", nonPostalResult);
    }

}
