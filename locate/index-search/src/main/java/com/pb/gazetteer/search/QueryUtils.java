package com.pb.gazetteer.search;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static com.pb.gazetteer.lucene.LuceneIndexerConstants.SEARCH_ADDRESS;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/6/13
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryUtils {
    private static final Logger logger = LogManager.getLogger(QueryUtils.class);

    public static final Pattern WHITESPACE = Pattern.compile("\\s+");
    public static final Pattern APOSTROPHE = Pattern.compile("'s");
    public static final Pattern ENDS_WITH_S = Pattern.compile("s$");
    public static final Pattern FULL_STOP = Pattern.compile("\\.");
    public static final Pattern COMMA = Pattern.compile(",");
    public static final Pattern HYPHEN = Pattern.compile("\\\\-");
    public static final Pattern RANGE_NUMBER = Pattern.compile("\\d+\\\\-\\d+");


    public static final String AND = " AND ";
    private static final String OR = " OR ";
    public static final String EXPLICIT_WHITESPACE = " ";


    private static final Set<Character> SPECIAL_CHARACTERS = new TreeSet<Character>() {{
        add(Character.valueOf('^'));
        add(Character.valueOf('~'));
        add(Character.valueOf('/'));
        add(Character.valueOf('\\'));
        add(Character.valueOf('+'));
        add(Character.valueOf('-'));
        add(Character.valueOf('&'));
        add(Character.valueOf('|'));
        add(Character.valueOf('('));
        add(Character.valueOf(')'));
        add(Character.valueOf('{'));
        add(Character.valueOf('}'));
        add(Character.valueOf('['));
        add(Character.valueOf(']'));
        add(Character.valueOf(':'));
        add(Character.valueOf('!'));
    }};

    /**
     * Strips the full stop in query as we do not expect it to be part of the address.
     *
     * @param query
     * @return
     */
    public static String stripFullStop(String query) {
        return FULL_STOP.matcher(query).replaceAll("");
    }

    /**
     * Strip the commas from the string.
     *
     * @param info
     * @return
     */
    public static String stripCommas(String info) {
        return COMMA.matcher(info).replaceAll(" ");
    }

    /**
     * Replaces multiple white spaces.
     *
     * @param query
     * @return
     */
    public static String checkAndReplaceWhiteSpacesWithAnd(String query) {
        query = checkAndReplaceHyphenWithWhitespace(query);
        StringBuffer strModified = new StringBuffer(query.trim());
        return WHITESPACE.matcher(strModified).replaceAll(AND);

    }

    /**
     * Replaces hyphen with whitespace.
     *
     * @param query
     * @return
     */
    private static String checkAndReplaceHyphenWithWhitespace(String query) {
        StringBuffer strModified = new StringBuffer(query.trim());
        if(RANGE_NUMBER.matcher(strModified).find())
        {
            return HYPHEN.matcher(strModified).replaceAll(EXPLICIT_WHITESPACE);
        }
        return query;
    }

    public static String replaceWhiteSpaces(String query) {
        if ((query != null) && (!StringUtils.isEmpty(query))) {
            StringBuffer strModified = new StringBuffer(query);
            return WHITESPACE.matcher(strModified).replaceAll("");
        }
        return query;

    }


    public static String manageTokensEndingWithS(String token) {
        StringBuffer tk = new StringBuffer(token);

        if (ENDS_WITH_S.matcher(tk).find()) {
            if (APOSTROPHE.matcher(tk).find())
                return "(" + token + OR + checkAndApostrophe(token) + ")";
            else
                return "(" + token + OR + ENDS_WITH_S.matcher(tk).replaceAll("'s") + ")";
        } else
            return tk.toString();
    }

    /*
    * We are generating tokens separated by OR for 's and words ending with s
    */
    public static String replaceApostropheWordReplacement(String query) {
        StringTokenizer tokens = new StringTokenizer(query);
        StringBuffer modifiedElement = new StringBuffer();

        while (tokens.hasMoreElements()) {
            String token = tokens.nextToken();
            modifiedElement.append(manageTokensEndingWithS(token));
            modifiedElement.append(" ");
        }

        return modifiedElement.toString().trim();

    }

    public static String checkAndApostrophe(String query) {
        if ((query != null) && (!StringUtils.isEmpty(query))) {
            StringBuffer strModified = new StringBuffer(query);
            return APOSTROPHE.matcher(strModified).replaceAll("s");
        }
        return query;
    }

    public static String processUserQuery(String queryText) {

        if (!StringUtils.isEmpty(queryText)) {
            return replaceApostropheWordReplacement(
                    checkAndReplaceWhiteSpacesWithAnd(
                            stripFullStop(
                                    escapeSpecialCharacters(
                                            queryText.toLowerCase()))));
        } else {
            return queryText;
        }

    }

    /**
     * A debug function for reading the information indexed.
     *
     * @param ir
     * @throws IOException
     */
    public static void readIndex(IndexReader ir) throws IOException {


        if (ir != null) {
            if (logger.isDebugEnabled()) {
                logger.info(" Address count: " + ir.getDocCount(SEARCH_ADDRESS));
                logger.info(" Num Docs: " + ir.numDocs());
                logger.info(" Max Docs: " + ir.maxDoc());
            }


            for (int i = 0; i < ir.maxDoc(); i++) {

                Document doc = ir.document(i);

                if (doc != null) {
                    for (IndexableField fd : doc.getFields()) {
                        logger.info(fd.name() + " : ");
                        for (String value : doc.getValues(fd.name())) {
                            logger.info(value);
                        }

                        Terms terms = ir.getTermVector(i, fd.name());
                        if (terms != null && terms.size() > 0) {
                            TermsEnum termsEnum = terms.iterator(null); // access the terms for this field
                            BytesRef term = null;
                            logger.info(" The term vector that was created for it: ");
                            while ((term = termsEnum.next()) != null) {// explore the terms for this field
                                DocsEnum docsEnum = termsEnum.docs(null, null); // enumerate through documents, in this case only one
                                int docIdEnum;
                                while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                                    logger.info(term.utf8ToString() + " (loc) " + docIdEnum + " (freq) " + docsEnum.freq());
                                    //get the term frequency in the document
                                }
                            }
                        }


                    }
                }


            }
        }
    }

    /**
     * Function to escape special characters in the read line
     *
     * @param readLine
     * @return
     */
    public static String escapeSpecialCharacters(String readLine) {
        if ((readLine == null) || (readLine.isEmpty())) {
            return readLine;
        }
        Character ch = null;

        StringBuffer escapedString = new StringBuffer(readLine);
        int length = escapedString.length();
        for (int i = 0; i < length; i++) {
            ch = escapedString.charAt(i);

            if (SPECIAL_CHARACTERS.contains(ch)) {
                escapedString.insert(i, '\\');
                length = escapedString.length();
                i++;
            }
        }

        return escapedString.toString();
    }

}
