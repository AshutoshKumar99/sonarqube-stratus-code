package com.pb.gazetteer.lucene.search;

import com.pb.gazetteer.search.PostalCodesTokenizer;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UKQueryTokenizerPostCodeTestHelper
{
    /*
     * Postcode Tokens Tests
     */

    // Complete Post code with spaces
   @Test
    public void testIsTherePostalforOnePostalToken()
    {
        String text = "aplha beta AA99A gamma";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        boolean check = tokenizer.isTherePostal();
        assertTrue(check);
    }

    @Test
    public void testIsTherePostalforNoPostalCodes()
    {
        String text = "aplha beta ^&$^& gamma";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        boolean check = tokenizer.isTherePostal();
        assertFalse(check);
    }

    public void testCompletePostCodeFormatA9_9AA()
    {
        String text = "A9 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A9", "9AA", 2, postcodeTokens);
    }

    public void testCompletePostCodeFormatA99_9AA()
    {
        String text = "A99 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A99", "9AA", 2, postcodeTokens);
    }

    public void testCompletePostCodeFormatA9A_9AA()
    {
        String text = "A9A 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertEquals(2, postcodeTokens.size());
        assertEquals("A9A", postcodeTokens.get(0));
        assertEquals("9AA", postcodeTokens.get(1));
    }

    public void testCompletePostCodeFormatAA9_9AA()
    {
        String text = "AA9 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9", "9AA", 2, postcodeTokens);
    }

    public void testCompletePostCodeFormatAA99_9AA()
    {
        String text = "AA99 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA99", "9AA", 2, postcodeTokens);
    }

    public void testCompletePostCodeFormatAA9A_9AA()
    {
        String text = "AA9A 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "9AA", 2, postcodeTokens);
    }

    // Complete postcodes without spaces

    public void testCompletePostCodeFormatA99AA()
    {
        String text = "A99AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A9", "9AA", 2, postcodeTokens);
    }

    public void testCompletePostCodeFormatA999AA()
    {
        String text = "A999AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A99", "9AA", 2, postcodeTokens);
    }

    public void testCompletePostCodeFormatA9A9AA()
    {
        String text = "A9A9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A9A", "9AA", 2, postcodeTokens);
    }

    public void testCompletePostCodeFormatAA99AA()
    {
        String text = "AA99AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9", "9AA", 2, postcodeTokens);
    }

    public void testCompletePostCodeFormatAA999AA()
    {
        String text = "AA999AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA99", "9AA", 2, postcodeTokens);
    }

    public void testCompletePostCodeFormatAA9A9AA()
    {
        String text = "AA9A9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "9AA", 2, postcodeTokens);
    }

    // Outward postcode only

    public void testOutwardPostCodeFormatA9()
    {
        String text = "A9";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A9", "", 1, postcodeTokens);
    }

    public void testOutwardPostCodeFormatA99()
    {
        String text = "A99";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A99", "", 1, postcodeTokens);
    }

    public void testOutwardPostCodeFormatA9A()
    {
        String text = "A9A";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A9A", "", 1, postcodeTokens);
    }

    public void testOutwardPostCodeFormatAA9()
    {
        String text = "AA9";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9", "", 1, postcodeTokens);
    }

    public void testOutwardPostCodeFormatAA99()
    {
        String text = "AA99";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA99", "", 1, postcodeTokens);
    }

    public void testOutwardPostCodeFormatAA9A()
    {
        String text = "AA9A";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "", 1, postcodeTokens);
    }

    // Tests with text and post codes

    public void testStringWithStreetNameOutwardPostCode()
    {
        String text = "Test Street AA9A";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "", 1, postcodeTokens);
    }

    public void testStringWithStreetNameFullPostCode()
    {
        String text = "Test Street AA9A 9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "9AA", 2, postcodeTokens);
    }

    public void testStringWithStreetNameOutwardPostCodeAndSomeMore()
    {
        String text = "Test Street AA9A Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "", 1, postcodeTokens);
    }

    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9A()
    {
        String text = "Test Street AA9A 9AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "9AA", 2, postcodeTokens);
    }

    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA99()
    {
        String text = "Test Street AA99 9AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA99", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9()
    {
        String text = "Test Street AA9 9AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9A()
    {
        String text = "Test Street A9A 9AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A9A", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA99()
    {
        String text = "Test Street A99 9AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A99", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9()
    {
        String text = "Test Street A9 9AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A9", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9ANoSpace()
    {
        String text = "Test Street AA9A9AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "9AA", 2, postcodeTokens);
    }

    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA99NoSpace()
    {
        String text = "Test Street AA999AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA99", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatAA9NoSpace()
    {
        String text = "Test Street AA99AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9ANoSpace()
    {
        String text = "Test Street A9A9AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A9A", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA99NoSpace()
    {
        String text = "Test Street A999AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A99", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeAndSomeMoreFormatA9NoSpace()
    {
        String text = "Test Street A99AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("A9", "9AA", 2, postcodeTokens);
    }
    
    public void testStringWithStreetNameFullPostCodeNoSpace()
    {
        String text = "Test Street AA9A9AA";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "9AA", 2, postcodeTokens);
    }

    public void testStringWithStreetNameFullPostCodeAndSomeMoreNoSpace()
    {
        String text = "Test Street AA9A9AA Test Town";
        PostalCodesTokenizer tokenizer = new PostalCodesTokenizer(text);
        List<String> postcodeTokens = tokenizer.getPostcodeTokens();
        assertPostcodeTokens("AA9A", "9AA", 2, postcodeTokens);

    }

    private void assertPostcodeTokens(String outwardCode, String inwardCode,
        int expectedTokens, List<String> postcodeTokens)
    {
        assertEquals(expectedTokens, postcodeTokens.size());
        if (expectedTokens == 2)
        {
            assertEquals(outwardCode, postcodeTokens.get(0));
            assertEquals(inwardCode, postcodeTokens.get(1));
        }
        else if (expectedTokens == 1)
        {
            assertEquals(outwardCode, postcodeTokens.get(0));
        }
    }
}
