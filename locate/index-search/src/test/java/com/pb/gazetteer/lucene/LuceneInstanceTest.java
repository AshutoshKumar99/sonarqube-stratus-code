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

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;
import java.io.IOException;


import com.pb.gazetteer.search.SearchLogic;
import com.pb.gazetteer.webservice.LocateException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pb.gazetteer.UnitTestUtils;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest({IndexDir.class, LuceneInstanceTest.class})
public class LuceneInstanceTest
{
    File tempFile;

    @Before
    public void before() throws IOException
    {
        tempFile = File.createTempFile("LuceneInstanceTest", "testFile");
        tempFile.deleteOnExit();
    }

    @After
    public void after() {
        tempFile.delete();
    }

    @Test
    public void testSrs() {
        LuceneInstance instance = new LuceneInstance();
        instance.setSrs("testValue");
        assertEquals("testValue", instance.getSrs());
    }

    @Test
    public void testSearchLogic() {
        LuceneInstance instance = new LuceneInstance();
        instance.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
        assertEquals(SearchLogic.DEFAULT_LOGIC, instance.getSearchLogic());
    }

    @Test
    public void testCreateNewIndexDir() throws LocateException
    {
        IndexDir mockIndexDir;
        mockIndexDir = mock(IndexDir.class);
        mockStatic(IndexDir.class);
        when(IndexDir.createNewIndexDir(tempFile)).thenReturn(mockIndexDir);

        LuceneInstance instance = new LuceneInstance();
        instance.setLocalIndexDir(tempFile);
        IndexDir result = instance.createNewIndexDir();
        assertSame(mockIndexDir, result);
    }

    @Test
    public void testGetLatestReadyIndex() throws LocateException
    {
        LuceneIndexCleaner mockCleaner = mock(LuceneIndexCleaner.class);

        IndexDir mockIndexDir = mock(IndexDir.class);
        mockStatic(IndexDir.class);
        when(IndexDir.getLatestReadyIndex(tempFile, mockCleaner)).thenReturn(mockIndexDir);

        LuceneInstance instance = new LuceneInstance();
        instance.setLocalIndexDir(tempFile);
        IndexDir result = instance.getLatestReadyIndex(mockCleaner);
        assertSame(mockIndexDir, result);
    }
}
