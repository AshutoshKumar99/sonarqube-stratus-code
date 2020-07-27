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

import com.pb.custom.lucene.CustomAnalyzer;
import com.pb.gazetteer.*;
import com.pb.gazetteer.search.QueryUtils;
import com.pb.gazetteer.webservice.LocateException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest({LuceneIndexGenerator.class})
public class LuceneIndexGeneratorTest {
    private static String EOL = System.getProperty("line.separator");


    private static final byte[] input_3errors = (
            "address1,12.34,34.56" + EOL
                    + "address2,66.77,88.99" + EOL
                    + ",66.77,88.99" + EOL
                    + "address2,badvalue,88.99" + EOL
                    + "address2,66.77,badvalue" + EOL
    ).getBytes();

    private static final byte[] input_0errors = (
            "address1,12.34,34.56" + EOL
                    + "address2,66.77,88.99" + EOL
    ).getBytes();

    /**
     * + - && || ! ( ) { } [ ] ^ " ~ * ? : \
     */
    private static final byte[] input_special_characters = (
            "address~1 S,12.34,34.56" + EOL +
                    "address + 1 S,12.34,34.56" + EOL +
                    "address - 1 S,12.34,34.56" + EOL +
                    "address : 1 S,12.34,34.56" + EOL +
                    "address & 1 S,12.34,34.56" + EOL +
                    "address | 1 S,12.34,34.56" + EOL +
                    "address ! 1 S,12.34,34.56" + EOL +
                    "address ( 1 S,12.34,34.56" + EOL +
                    "address ) 1 S,12.34,34.56" + EOL +
                    "address { 1 S,12.34,34.56" + EOL +
                    "address } 1 S,12.34,34.56" + EOL +
                    "address [ 1 S,12.34,34.56" + EOL +
                    "address ] 1 S,12.34,34.56" + EOL +
                    "address ^ 1 S,12.34,34.56" + EOL +
                    "address * 1 S,12.34,34.56" + EOL +
                    "address ? 1 S,12.34,34.56" + EOL +
                    "address / 1 S,12.34,34.56" + EOL +
                    "address \\ 1 S,12.34,34.56" + EOL +
                    "address \' 1 S,12.34,34.56" + EOL

    ).getBytes();

    private static final byte[] input_comma_characters = (
            "\"address, india, abcd 123,245\",12.34,34.56" + EOL
                    + "\"Flat 1, 10 Bramshurst, Abbey Road, London, NW8 0AX\",525854,183613" + EOL +
                    "\"Flat 1 Bramshurst, Abbey Road, London, NW8 0AX\",525854,183613" + EOL
    ).getBytes();

    private LuceneInstance mockInstance;
    private IndexDir mockIndexDir;
    private IndexWriterConfig mockIndexWriterConf;

    @Before
    public void before() throws Exception {
        Directory mockDir = mock(Directory.class);
        mockIndexWriterConf = new IndexWriterConfig(Version.LUCENE_45, new CustomAnalyzer());

        mockIndexDir = mock(IndexDir.class);
        when(mockIndexDir.getLuceneDir()).thenReturn(mockDir);
        mockInstance = mock(LuceneInstance.class);
        when(mockInstance.createNewIndexDir()).thenReturn(mockIndexDir);

        IndexWriter mockWriter = mock(IndexWriter.class);
        whenNew(IndexWriter.class)
                .withParameterTypes(Directory.class, IndexWriterConfig.class)
                .withArguments(same(mockDir), any(IndexWriterConfig.class)).thenReturn(mockWriter);

    }

    @Test
    public void testSpecialSuccess() throws IOException, LocateException, IndexCreationException {
        IndexLock mockIndexLock = mock(IndexLock.class);
        when(mockIndexDir.acquireIndexLock()).thenReturn(mockIndexLock);

        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(10);

        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_special_characters));
        assertTrue(response.getSuccess());
        assertNull(response.getFailureCode());
        assertEquals(19, response.getRowAddedCnt());
        verify(mockIndexDir).markReady();
        verify(mockIndexLock).release();
    }

    @Test
    public void testCommaSuccess() throws IOException, LocateException, IndexCreationException {
        IndexLock mockIndexLock = mock(IndexLock.class);
        when(mockIndexDir.acquireIndexLock()).thenReturn(mockIndexLock);

        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(10);

        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_comma_characters));
        assertTrue(response.getSuccess());
        assertNull(response.getFailureCode());
        assertEquals(3, response.getRowAddedCnt());

        verify(mockIndexDir).markReady();
        verify(mockIndexLock).release();

    }


    @Test
    public void testSuccess() throws IOException, LocateException, IndexCreationException {
        IndexLock mockIndexLock = mock(IndexLock.class);
        when(mockIndexDir.acquireIndexLock()).thenReturn(mockIndexLock);

        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(10);

        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_3errors));
        assertTrue(response.getSuccess());
        assertNull(response.getFailureCode());
        assertEquals(2, response.getRowAddedCnt());
        List<LineFailure> lineFailures = response.getLineFailures();
        assertEquals(3, lineFailures.size());
        assertEquals(3, lineFailures.get(0).getLine());
        assertEquals(LineFailureCode.EMPTY_ADDRESS_FIELD, lineFailures.get(0).getFailureCode());
        assertEquals(4, lineFailures.get(1).getLine());
        assertEquals(LineFailureCode.INVALID_X_FIELD, lineFailures.get(1).getFailureCode());
        assertEquals(5, lineFailures.get(2).getLine());
        assertEquals(LineFailureCode.INVALID_Y_FIELD, lineFailures.get(2).getFailureCode());

        verify(mockIndexDir).markReady();
        verify(mockIndexLock).release();
    }

    @Test(expected = LocateException.class)
    public void testError_cantGetBuildLock() throws IndexCreationException, IOException, LocateException {
        when(mockIndexDir.acquireIndexLock()).thenThrow(new IndexCreationException());

        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(10);

        LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_3errors));
    }

    @Test
    public void testError_maxFailures_aboveThreshold() throws IOException, LocateException {
        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(1);

        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_3errors));
        assertFalse(response.getSuccess());
        assertEquals(FailureCode.EXCEEDED_MAX_FAILURES, response.getFailureCode());
        assertEquals(0, response.getRowAddedCnt());
        List<LineFailure> lineFailures = response.getLineFailures();
        assertEquals(2, lineFailures.size());
        assertEquals(3, lineFailures.get(0).getLine());
        assertEquals(LineFailureCode.EMPTY_ADDRESS_FIELD, lineFailures.get(0).getFailureCode());
        assertEquals(4, lineFailures.get(1).getLine());
        assertEquals(LineFailureCode.INVALID_X_FIELD, lineFailures.get(1).getFailureCode());

        verify(mockIndexDir).delete();
    }

    @Test
    public void testError_maxFailures_belowThreshold() throws IOException, LocateException {
        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(3);
        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_3errors));

        assertTrue(response.getSuccess());
        assertNull(response.getFailureCode());
        assertEquals(2, response.getRowAddedCnt());
        List<LineFailure> lineFailures = response.getLineFailures();
        assertEquals(3, lineFailures.size());
        assertEquals(3, lineFailures.get(0).getLine());
        assertEquals(LineFailureCode.EMPTY_ADDRESS_FIELD, lineFailures.get(0).getFailureCode());
        assertEquals(4, lineFailures.get(1).getLine());
        assertEquals(LineFailureCode.INVALID_X_FIELD, lineFailures.get(1).getFailureCode());
        assertEquals(5, lineFailures.get(2).getLine());
        assertEquals(LineFailureCode.INVALID_Y_FIELD, lineFailures.get(2).getFailureCode());

        verify(mockIndexDir).markReady();
    }

    @Test
    public void testError_maxFailures_noErrors() throws IOException, LocateException {
        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(0);

        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_0errors));
        assertTrue(response.getSuccess());
        assertNull(response.getFailureCode());
        assertEquals(2, response.getRowAddedCnt());
        assertNull(response.getLineFailures());

        verify(mockIndexDir).markReady();
    }

    @Test
    public void testError_missingAddressColumn() throws IOException, LocateException {
        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(4);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(1);

        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_3errors));
        assertFalse(response.getSuccess());
        assertEquals(FailureCode.EXCEEDED_MAX_FAILURES, response.getFailureCode());
        assertEquals(0, response.getRowAddedCnt());
        List<LineFailure> lineFailures = response.getLineFailures();
        assertEquals(2, lineFailures.size());
        assertEquals(1, lineFailures.get(0).getLine());
        assertEquals(LineFailureCode.MISSING_ADDRESS_FIELD, lineFailures.get(0).getFailureCode());
        assertEquals(2, lineFailures.get(1).getLine());
        assertEquals(LineFailureCode.MISSING_ADDRESS_FIELD, lineFailures.get(1).getFailureCode());

        verify(mockIndexDir).delete();
    }

    @Test
    public void testError_missingXColumn() throws IOException, LocateException {
        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(4);
        parameters.setyColumn(2);
        parameters.setMaxFailures(1);

        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_3errors));
        assertFalse(response.getSuccess());
        assertEquals(FailureCode.EXCEEDED_MAX_FAILURES, response.getFailureCode());
        assertEquals(0, response.getRowAddedCnt());
        List<LineFailure> lineFailures = response.getLineFailures();
        assertEquals(2, lineFailures.size());
        assertEquals(1, lineFailures.get(0).getLine());
        assertEquals(LineFailureCode.MISSING_X_FIELD, lineFailures.get(0).getFailureCode());
        assertEquals(2, lineFailures.get(1).getLine());
        assertEquals(LineFailureCode.MISSING_X_FIELD, lineFailures.get(1).getFailureCode());

        verify(mockIndexDir).delete();
    }

    @Test
    public void testError_missingYColumn() throws IOException, LocateException {
        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(1);
        parameters.setyColumn(4);
        parameters.setMaxFailures(1);

        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_3errors));
        assertFalse(response.getSuccess());
        assertEquals(FailureCode.EXCEEDED_MAX_FAILURES, response.getFailureCode());
        assertEquals(0, response.getRowAddedCnt());
        List<LineFailure> lineFailures = response.getLineFailures();
        assertEquals(2, lineFailures.size());
        assertEquals(1, lineFailures.get(0).getLine());
        assertEquals(LineFailureCode.MISSING_Y_FIELD, lineFailures.get(0).getFailureCode());
        assertEquals(2, lineFailures.get(1).getLine());
        assertEquals(LineFailureCode.MISSING_Y_FIELD, lineFailures.get(1).getFailureCode());

        verify(mockIndexDir).delete();
    }

    @Test
    public void testError_noRowsAdded() throws IOException, LocateException {
        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(5);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(50);

        PopulateResponse response = LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_3errors));
        assertFalse(response.getSuccess());
        assertEquals(FailureCode.NO_RECORDS_ADDED, response.getFailureCode());
        assertEquals(0, response.getRowAddedCnt());
        List<LineFailure> lineFailures = response.getLineFailures();
        assertEquals(5, lineFailures.size());
        for (int i = 0; i < lineFailures.size(); i++) {
            LineFailure lineFailure = lineFailures.get(i);
            assertEquals(i + 1, lineFailure.getLine());
            assertEquals(LineFailureCode.MISSING_ADDRESS_FIELD, lineFailure.getFailureCode());
        }

        verify(mockIndexDir).delete();
    }

    @Test(expected = LocateException.class)
    public void testError_failedToCreateIndexDir() throws IOException, LocateException {
        doThrow(new LocateException()).when(mockInstance).createNewIndexDir();

        PopulateParameters parameters = new PopulateParameters();
        parameters.setDelimiter(",");
        parameters.setGazetteerName("gaz1");
        parameters.setTenantName("ten1");
        parameters.setAddressColumn(0);
        parameters.setxColumn(1);
        parameters.setyColumn(2);
        parameters.setMaxFailures(10);

        LuceneIndexGenerator.generate(mockInstance, parameters, new ByteArrayInputStream(input_3errors));
    }
}
