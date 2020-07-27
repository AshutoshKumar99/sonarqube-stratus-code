package com.pb.gazetteer.webservice;

import com.pb.gazetteer.ConfigurationException;
import com.pb.gazetteer.lucene.LuceneIndexSearchFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({System.class, ApplicationContextListener.class})
@PowerMockIgnore("javax.management.*")
public class ApplicationContextListenerTest
{
    private static String m_testConfigDir;
    private ApplicationContextListener m_aclUnderTest = new ApplicationContextListener();
    private ServletContext m_mockContext = mock(ServletContext.class);

    @BeforeClass
    public static void beforeClass() throws IOException
    {
        m_testConfigDir = File.createTempFile("ApplicationContextListenerTest", "tst").getParentFile().getAbsolutePath();
    }

    @Before
    public void before() {
        PowerMockito.mockStatic(System.class);
    }

    @After
    public void after()
    {
        //need to cleanup the static reference, otherwise subsequent tests won't run properly
        m_aclUnderTest.contextDestroyed(new ServletContextEvent(m_mockContext));
    }

    @Test
    public void testConfigDir_webXml() throws Exception
    {
        LocateConfigProvider mockProvider = mock(LocateConfigProvider.class);
        when(System.getProperty("stratus.customer.config.dir")).thenReturn(null);
        when(m_mockContext.getInitParameter("stratus.customer.config.dir")).thenReturn(m_testConfigDir);
        ServletContextEvent event = new ServletContextEvent(m_mockContext);
        whenNew(LocateConfigProvider.class).withAnyArguments().thenReturn(mockProvider);

        //test code
        m_aclUnderTest.contextInitialized(event);

        //verify
        verify(m_mockContext, times(1)).setAttribute(ApplicationContextListener.PROVIDER_KEY, mockProvider);
        verifyNew(LocateConfigProvider.class).withArguments(eq(m_testConfigDir), anyMap());
    }

    @Test
    public void testConfigDir_system() throws Exception
    {
        LocateConfigProvider mockProvider = mock(LocateConfigProvider.class);
        when(System.getProperty("stratus.customer.config.dir")).thenReturn(m_testConfigDir);
        when(m_mockContext.getInitParameter("stratus.customer.config.dir")).thenReturn("123");
        ServletContextEvent event = new ServletContextEvent(m_mockContext);
        whenNew(LocateConfigProvider.class).withAnyArguments().thenReturn(mockProvider);

        //test code
        m_aclUnderTest.contextInitialized(event);

        //verify
        verify(m_mockContext, times(1)).setAttribute(ApplicationContextListener.PROVIDER_KEY, mockProvider);
        verifyNew(LocateConfigProvider.class).withArguments(eq(m_testConfigDir), anyMap());
    }

    @Test(expected = ConfigurationException.class)
    public void testConfigDir_none()
    {
        when(System.getProperty("stratus.customer.config.dir")).thenReturn(null);
        when(m_mockContext.getInitParameter("stratus.customer.config.dir")).thenReturn(null);
        ServletContextEvent event = new ServletContextEvent(m_mockContext);

        //test code
        m_aclUnderTest.contextInitialized(event);
    }

    @Test
    public void testDelay_default() throws Exception
    {
        LuceneIndexSearchFactory mockFactory = mock(LuceneIndexSearchFactory.class);
        when(System.getProperty("stratus.customer.config.dir")).thenReturn(m_testConfigDir);
        when(m_mockContext.getInitParameter("com.pb.gazetteer.lucene.LuceneIndexCleaner.delay")).thenReturn(null);
        whenNew(LuceneIndexSearchFactory.class).withArguments(anyInt(), anyInt()).thenReturn(mockFactory);

        //test code
        m_aclUnderTest.contextInitialized(new ServletContextEvent(m_mockContext));

        //verify
        verifyNew(LuceneIndexSearchFactory.class).withArguments(eq(-1), eq(-1));
    }

    @Test
    public void testDelay_supplied() throws Exception
    {
        LuceneIndexSearchFactory mockFactory = mock(LuceneIndexSearchFactory.class);
        when(System.getProperty("stratus.customer.config.dir")).thenReturn(m_testConfigDir);
        when(m_mockContext.getInitParameter("com.pb.gazetteer.lucene.LuceneIndexCleaner.delay")).thenReturn("543");
        whenNew(LuceneIndexSearchFactory.class).withArguments(anyInt(), anyInt()).thenReturn(mockFactory);

        //test code
        m_aclUnderTest.contextInitialized(new ServletContextEvent(m_mockContext));

        //verify
        verifyNew(LuceneIndexSearchFactory.class).withArguments(eq(543), eq(-1));
    }

    @Test(expected = ConfigurationException.class)
    public void testDelay_invalid() throws Exception
    {
        LuceneIndexSearchFactory mockFactory = mock(LuceneIndexSearchFactory.class);
        when(System.getProperty("stratus.customer.config.dir")).thenReturn(m_testConfigDir);
        when(m_mockContext.getInitParameter("com.pb.gazetteer.lucene.LuceneIndexCleaner.delay")).thenReturn("sfd");
        whenNew(LuceneIndexSearchFactory.class).withArguments(anyInt(), anyInt()).thenReturn(mockFactory);

        //test code
        m_aclUnderTest.contextInitialized(new ServletContextEvent(m_mockContext));
    }

    @Test
    public void testThreadCnt_default() throws Exception
    {
        LuceneIndexSearchFactory mockFactory = mock(LuceneIndexSearchFactory.class);
        when(System.getProperty("stratus.customer.config.dir")).thenReturn(m_testConfigDir);
        when(m_mockContext.getInitParameter("com.pb.gazetteer.lucene.LuceneIndexCleaner.threadPoolSize")).thenReturn(null);
        whenNew(LuceneIndexSearchFactory.class).withArguments(anyInt(), anyInt()).thenReturn(mockFactory);

        //test code
        m_aclUnderTest.contextInitialized(new ServletContextEvent(m_mockContext));

        //verify
        verifyNew(LuceneIndexSearchFactory.class).withArguments(eq(-1), eq(-1));
    }

    @Test
    public void testThreadCnt_supplied() throws Exception
    {
        LuceneIndexSearchFactory mockFactory = mock(LuceneIndexSearchFactory.class);
        when(System.getProperty("stratus.customer.config.dir")).thenReturn(m_testConfigDir);
        when(m_mockContext.getInitParameter("com.pb.gazetteer.lucene.LuceneIndexCleaner.threadPoolSize")).thenReturn("543");
        whenNew(LuceneIndexSearchFactory.class).withArguments(anyInt(), anyInt()).thenReturn(mockFactory);

        //test code
        m_aclUnderTest.contextInitialized(new ServletContextEvent(m_mockContext));

        //verify
        verifyNew(LuceneIndexSearchFactory.class).withArguments(eq(-1), eq(543));
    }

    @Test(expected = ConfigurationException.class)
    public void testThreadCnt_invalid() throws Exception
    {
        LuceneIndexSearchFactory mockFactory = mock(LuceneIndexSearchFactory.class);
        when(System.getProperty("stratus.customer.config.dir")).thenReturn(m_testConfigDir);
        when(m_mockContext.getInitParameter("com.pb.gazetteer.lucene.LuceneIndexCleaner.threadPoolSize")).thenReturn("sfd");
        whenNew(LuceneIndexSearchFactory.class).withArguments(anyInt(), anyInt()).thenReturn(mockFactory);

        //test code
        m_aclUnderTest.contextInitialized(new ServletContextEvent(m_mockContext));
    }

    @Test
    public void testDestroy() throws Exception
    {
        LocateConfigProvider mockProvider = mock(LocateConfigProvider.class);
        when(m_mockContext.getAttribute(ApplicationContextListener.PROVIDER_KEY)).thenReturn(mockProvider);

        //test code
        m_aclUnderTest.contextDestroyed(new ServletContextEvent(m_mockContext));

        //verify
        verify(m_mockContext, times(1)).removeAttribute(ApplicationContextListener.PROVIDER_KEY);
        verify(mockProvider).release();
    }

}
