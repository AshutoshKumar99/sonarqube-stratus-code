package com.pb.gazetteer.lucene;

import junit.framework.TestCase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.pb.gazetteer.lucene.NullDirectory;

public class NullDirectoryTest extends TestCase
{

    public NullDirectoryTest(String testName)
    {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testGetInstance() throws Exception
    {
        Directory directory = NullDirectory.newInstance();
        assertTrue(directory instanceof RAMDirectory);
        IndexReader reader = DirectoryReader.open(directory);
        assertEquals(0, reader.numDocs());
        reader.close();
    }
}
