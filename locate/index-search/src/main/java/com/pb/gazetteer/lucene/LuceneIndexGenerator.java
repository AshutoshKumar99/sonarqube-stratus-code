/*******************************************************************************
 * Copyright (c) 2011, Pitney Bowes Software Inc.
 * All  rights reserved.
 * Confidential Property of Pitney Bowes Software Inc.
 *
 * $Author: $
 * $Revision: $
 * $LastChangedDate: $
 *
 * $HeadURL: $
 ******************************************************************************/

package com.pb.gazetteer.lucene;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import com.pb.custom.lucene.CustomAnalyzer;
import com.pb.custom.lucene.CustomTextField;
import com.pb.gazetteer.*;
import com.pb.gazetteer.webservice.LocateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.pb.gazetteer.lucene.LuceneIndexerConstants.*;

public class LuceneIndexGenerator {
    private static final Logger log = LogManager.getLogger(LuceneIndexGenerator.class);
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    /**
     * @param instance
     * @param populateParameters
     * @param in
     * @return
     * @throws LocateException
     * @throws IOException
     */
    public static PopulateResponse generate(LuceneInstance instance,
                                            PopulateParameters populateParameters, InputStream in) throws LocateException, IOException {
        /**
         * First step is to create a new sequenced directory for the index.
         */
        IndexDir indexDir = instance.createNewIndexDir();

        IndexLock lock = null;
        try {
            lock = indexDir.acquireIndexLock();

            /**
             * Using my own analyzer to define the tokenization process.
             */
            Analyzer analyzer = new CustomAnalyzer();

            Directory dir = indexDir.getLuceneDir();
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_45, analyzer);
            IndexWriter writer = new IndexWriter(dir, iwc);

            PopulateResponse response = new PopulateResponse();
            response.setSuccess(false);
            try {

                /**
                 * Make sure the information being read preserves the character set.
                 */
                CSVReader reader = new CSVReader(new InputStreamReader(in, "UTF-8"),
                        populateParameters.getDelimiter().charAt(0), CSVParser.DEFAULT_QUOTE_CHARACTER, '\0');

                log.info(" Indexing started at: " + new Date());

                try {
                    int successCnt = 0;
                    int errorCnt = 0;
                    int lineCnt = 0;
                    String[] nextLine;
                    while (((nextLine = reader.readNext()) != null) && errorCnt <= populateParameters.getMaxFailures()) {
                        lineCnt++;
                        if (validateLine(nextLine, populateParameters, lineCnt, response)) {
                            addDocument(writer, nextLine, populateParameters, lineCnt);
                            successCnt++;
                        } else {
                            errorCnt++;
                        }
                    }
                    log.info("Indexing of '" + lineCnt + "' records is completed successfully and now closing the reader and writer.");
                    response.setRowAddedCnt(successCnt);

                    if (errorCnt > populateParameters.getMaxFailures()) {
                        //since this is a complete failure, we will report no row added
                        response.setRowAddedCnt(0);
                        response.setFailureCode(FailureCode.EXCEEDED_MAX_FAILURES);
                    } else if (response.getRowAddedCnt() == 0) {
                        response.setFailureCode(FailureCode.NO_RECORDS_ADDED);
                    } else {
                        response.setSuccess(true);
                    }

                    return response;
                } finally {
                    IOUtils.closeQuietly(reader);
                }
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    //ignore... tried to close.
                    log.error(" Error: " + e);
                }

                if (response.getSuccess()) {
                    indexDir.markReady();
                } else {
                    indexDir.delete();
                }
            }
        } catch (IndexCreationException e) {
            throw new LocateException(e);
        } finally {
            if (lock != null) {
                log.info(" Indexing completed at: " + new Date());
                lock.release();
            }
        }
    }

    /**
     * @param line
     * @param parameters
     * @param lineNum
     * @param response
     * @return
     */
    private static boolean validateLine(String[] line, PopulateParameters parameters,
                                        int lineNum, PopulateResponse response) {
        try {
            if (StringUtils.isBlank(line[parameters.getAddressColumn()])) {
                log.error("Index line validation error: empty address field");
                addLineFailure(response, LineFailureCode.EMPTY_ADDRESS_FIELD, lineNum);
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Index line validation error: missing address field");
            addLineFailure(response, LineFailureCode.MISSING_ADDRESS_FIELD, lineNum);
            return false;
        }

        try {
            Double.parseDouble(line[parameters.getxColumn()]);
        } catch (ArrayIndexOutOfBoundsException e) {
            addLineFailure(response, LineFailureCode.MISSING_X_FIELD, lineNum);
            log.error("Index line validation error: missing x field", e);
            return false;
        } catch (NumberFormatException e) {
            addLineFailure(response, LineFailureCode.INVALID_X_FIELD, lineNum);
            log.error("Index line validation error: invalid x field", e);
            return false;
        }

        try {
            Double.parseDouble(line[parameters.getyColumn()]);
        } catch (ArrayIndexOutOfBoundsException e) {
            addLineFailure(response, LineFailureCode.MISSING_Y_FIELD, lineNum);
            log.error("Index line validation error: missing y field");
            return false;
        } catch (NumberFormatException e) {
            addLineFailure(response, LineFailureCode.INVALID_Y_FIELD, lineNum);
            log.error("Index line validation error: invalid y field", e);
            return false;
        }

        return true;
    }

    /**
     * @param response
     * @param f
     * @param lineNum
     */
    private static void addLineFailure(PopulateResponse response, LineFailureCode f, int lineNum) {
        List<LineFailure> lineFailures = response.getLineFailures();
        if (lineFailures == null) {
            lineFailures = new ArrayList<LineFailure>();
            response.setLineFailures(lineFailures);
        }

        lineFailures.add(new LineFailure(f, lineNum));
    }

    /**
     * @param writer
     * @param line
     * @param populateParameters
     * @param lineCnt
     * @throws IOException
     */
    private static void addDocument(IndexWriter writer, String[] line,
                                    PopulateParameters populateParameters, int lineCnt) throws IOException {
        Document document = new Document();
        //Escape the characters.
        String address = line[populateParameters.getAddressColumn()];

        FieldType fldType = new FieldType();
        fldType.setIndexed(false);
        fldType.setStored(true);
        fldType.setNumericType(FieldType.NumericType.INT);
        fldType.setNumericPrecisionStep(1);

        document.add(new IntField(SEARCH_ID, lineCnt, fldType));

        /**
         * Introduced my own custom text field for making sure the term vectors are
         * indexed.
         */
        document.add(new CustomTextField(SEARCH_ADDRESS, address, Field.Store.YES));

        document.add(new StringField(SEARCH_X, line[populateParameters.getxColumn()], Field.Store.YES));
        document.add(new StringField(SEARCH_Y, line[populateParameters.getyColumn()], Field.Store.YES));

        writer.addDocument(document);
    }

    private static String removeWhitespace(String s) {
        return WHITESPACE_PATTERN.matcher(s).replaceAll(" ");
    }

}
