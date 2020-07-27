package com.pb.gazetteer.lucene;

import com.pb.gazetteer.NoIndexFoundException;
import com.pb.gazetteer.search.SearchLogic;
import org.apache.lucene.store.Directory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest({LuceneIndexSearchFactory.class})
public class LuceneIndexSearchFactoryTest
{
    private String TEST_SRS = "testSRS";
    private String TEST_SEARCH_LOGIC = "testSearchLogic";

    @Mock
    LuceneIndexCleaner mockCleaner;
    @Mock
    LuceneInstance mockLuceneInstance;
    @Mock
    LuceneIndexSearch mockIndexSearch;
    @Mock
    private IndexDir mockIndexDir;
    @Mock
    private Directory mockDirectory;

    LuceneIndexSearchFactory factory;


    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        // mock constructors
        whenNew(LuceneIndexSearch.class).withNoArguments().thenReturn(mockIndexSearch);
        whenNew(LuceneIndexCleaner.class).withArguments(anyInt(), anyInt()).thenReturn(mockCleaner);

        // configure mocks
        when(mockLuceneInstance.getLatestReadyIndex(mockCleaner)).thenReturn(mockIndexDir);
        when(mockLuceneInstance.getSrs()).thenReturn(TEST_SRS);
        when(mockLuceneInstance.getSearchLogic()).thenReturn(SearchLogic.DEFAULT_LOGIC);

        when(mockIndexDir.getLuceneDir()).thenReturn(mockDirectory);

        factory = spy(new LuceneIndexSearchFactory(1, 1));
    }


    @Test
    public void test_shutdown()
    {
        factory.shutdown();
        verify(mockCleaner).shutdown();
    }

    @Test
    public void test_success() {
        factory.getIndex(mockLuceneInstance);

        verify(mockIndexSearch).setDirectory(mockDirectory);
        verify(mockIndexSearch).setSrs(TEST_SRS);
        verify(mockIndexSearch).setSearchLogic(SearchLogic.DEFAULT_LOGIC);
    }

    @Test (expected = NoIndexFoundException.class)
    public void test_errorGettingLatestDir() {
        //override to throw exception, and confirm it gets passed through
        when(mockLuceneInstance.getLatestReadyIndex(mockCleaner)).thenThrow(new NoIndexFoundException());
        factory.getIndex(mockLuceneInstance);
    }
}
