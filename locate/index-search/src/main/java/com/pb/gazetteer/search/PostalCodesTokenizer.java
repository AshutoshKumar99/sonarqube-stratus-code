package com.pb.gazetteer.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class tokenizes the query string as follows: 1. Finds the UK Post codes
 * and returns the tokenized out and in post codes in a given string 2. Removes
 * the Post codes and returns the list of tokens for rest of the query
 */
public class PostalCodesTokenizer {
    public static Pattern COMPLETE_POSTCODE_PATTERN =
            Pattern.compile("([A-Za-z]\\d\\d|[A-Za-z]\\d[A-Za-z]|[A-Za-z]\\d"
                    + "|[A-Za-z][A-Za-z]\\d[A-Za-z]"
                    + "|[A-Za-z][A-Za-z]\\d\\d|[A-Za-z][A-Za-z]\\d)"
                    + "\\s?(\\d[A-Za-z][A-Za-z]|\\d[A-Za-z]|\\d)");

    private static String OUTWARD_POSTCODE =
            "^[A-Za-z]\\d$|^[A-Za-z]\\d\\d$|^[A-Za-z]\\d[A-Za-z]$|^[A-Za-z][A-Za-z]\\d$"
                    + "|^[A-Za-z][A-Za-z]\\d\\d$|^[A-Za-z][A-Za-z]\\d[A-Za-z]$";

    /**
     * Danish postal code.
     */
    // A generalised regex for finding postcodes.
    private static final Pattern POSTCODE_PATTERN = Pattern.compile(
            "([A-Za-z]\\d|[A-Za-z]\\d\\d|" +
                    "[A-Za-z]\\d[A-Za-z]|" +
                    "[A-Za-z][A-Za-z]\\d" +
                    "|[A-Za-z][A-Za-z]\\d\\d|" +
                    "[A-Za-z][A-Za-z]\\d[A-Za-z]) ?(\\d[A-Za-z][A-Za-z])");


    public static Pattern OUTWARD_POSTCODE_PATTERN = Pattern.compile(OUTWARD_POSTCODE);

    /**
     * Sets the query text.
     * @param queryText
     */
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    private String queryText;

    public PostalCodesTokenizer(String queryText) {
        this.queryText = queryText;
    }

    /**
     * Checks the presence of Postal codes.
     * @return
     */
    public boolean isTherePostal() {
        if ((this.queryText != null) && (!this.queryText.isEmpty()))
            return OUTWARD_POSTCODE_PATTERN.matcher(this.queryText).find() || COMPLETE_POSTCODE_PATTERN.matcher(this.queryText).find();
        return false;
    }


    /**
     * Checks the presence of Danish Postal codes.
     * @return
     */
    public boolean isThereDanishPostal() {
        if ((this.queryText != null) && (!this.queryText.isEmpty()))
            return POSTCODE_PATTERN.matcher(this.queryText).find();
        return false;
    }

    /**
     * Get a list of postcode tokens from a string of text.
     *
     * @return List of postcode tokens
     */
    public List<String> getPostcodeTokens() {

        List<String> postcodes = new ArrayList<String>();

        if (this.queryText.matches(OUTWARD_POSTCODE)) {
            postcodes.add(this.queryText);
        } else {
            postcodes = getCompletePostCodeTokens();
        }

        return postcodes;
    }

    // Get a list of postcode tokens from a string of text.
    public List<String> getDanishPostcodeTokens()
    {
        Matcher matcher = POSTCODE_PATTERN.matcher(this.queryText);
        List<String> postcodes = new ArrayList();

        while (matcher.find())
        {
            for (int i = 1; i <= matcher.groupCount(); i++)
            {
                postcodes.add(matcher.group(i));
            }
        }
        return postcodes;
    }



    /*
     * Remove any postcode tokens from a string. For example:
     * "WD3 3TB earlobe bicycle" would return " earlobe bicycle"
     *
     * @return String of text without the postcode
     */
    public String removePostCodeTokensFromInputString(){

        String query = removePostcodeTokensFromString(this.queryText);

        query = QueryUtils.processUserQuery(query);

        return query;
    }




    private  String removePostcodeTokensFromString(String query) {
        String modifiedQuery = query;

        if (this.queryText.matches(OUTWARD_POSTCODE)) {
            modifiedQuery = this.queryText.replaceAll(OUTWARD_POSTCODE, "");
        }
        else if(POSTCODE_PATTERN.matcher(this.queryText).find()){
            modifiedQuery = removePostCodeTokens(this.queryText, POSTCODE_PATTERN.matcher(this.queryText));
        }
        else {
            Matcher matcher = COMPLETE_POSTCODE_PATTERN.matcher(this.queryText);
            modifiedQuery = removePostCodeTokens(this.queryText, matcher);
        }

        return modifiedQuery;
    }

    private List<String> getCompletePostCodeTokens() {
        Matcher matcher = COMPLETE_POSTCODE_PATTERN.matcher(this.queryText);
        List<String> postcodes = new ArrayList<String>();

        if (matcher.matches()) {
            postcodes = tokenize(matcher);
        } else {
            while (matcher.find()) {
                postcodes = tokenize(matcher);
            }
        }
        return postcodes;
    }

    private List<String> tokenize(Matcher matcher) {
        List<String> postcodes = new ArrayList<String>();
        for (int i = 1; i <= matcher.groupCount(); i++) {
            String postcodeToken = matcher.group(i);
            if (postcodeToken != null) {
                postcodes.add(postcodeToken);
            }
        }
        return postcodes;
    }

    private String removePostCodeTokens(String query, Matcher matcher) {
        String modifiedString = query;

        if (matcher.matches()) {
            modifiedString = remove(query, matcher);
        } else {
            while (matcher.find()) {
                modifiedString = remove(query, matcher);
            }
        }
        return modifiedString;
    }

    private String remove(String query, Matcher matcher) {
        String modifiedQuery = query;
        for (int i = 1; i <= matcher.groupCount(); i++) {
            String postcodeToken = matcher.group(i);
            if (postcodeToken != null) {
                modifiedQuery = modifiedQuery.replace(postcodeToken, "");
            }
        }

        return modifiedQuery;

    }
}
