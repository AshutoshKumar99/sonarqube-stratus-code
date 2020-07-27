package com.pb.gazetteer.resources;

import com.pb.custom.lucene.CustomAnalyzer;
import com.pb.gazetteer.Address;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.pb.gazetteer.lucene.LuceneIndexerConstants.*;

public class TestDirectory
{
    private static final Logger log = LogManager.getLogger(TestDirectory.class);

    private final static Analyzer analyzer = new CustomAnalyzer();
    private final static IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_45, analyzer);

    public static Directory newInstance() throws IOException
    {
        Directory directory = new RAMDirectory();
        IndexWriter writer =
                new IndexWriter(directory, iwc);


        Address address1 =
            new Address(
                "1",
                "Pitney Bowes Mapinfo, 3rd Floor, 186 City Road, London. EC1V 2NT",
                532549.0, 182704.0, "epsg:1234");
        Address address2 =
            new Address("2",
                "Mapinfo GDC, 3rd Floor, 69 Wilson Street, London. EC2A 2BB",
                532969.0, 182077.0, "epsg:1234");
        addDocument(writer, address1);
        addDocument(writer, address2);

        writer.commit();
        writer.close();
        return directory;
    }
    public static Directory searchDirectory() throws IOException
    {
        Directory directory = new RAMDirectory();

        IndexWriter writer =
                new IndexWriter(directory, iwc);

        addAddresses(writer, getAddressTestData());

        writer.commit();
        writer.close();
        return directory;
    }

    private static List<String> getAddressTestData() throws IOException
	{
		return IOUtils.readLines(TestDirectory.class.getResourceAsStream("addressData.txt"), "UTF-8");
    }

    @SuppressWarnings("unchecked")
    private static void addAddresses(IndexWriter writer, List<String> addresses) throws IOException
    {
        for (String address : addresses)
        {
           String[] addressTokens = address.split(",",2);
           Address newAddress =
                    new Address(
                            addressTokens[0],
                            addressTokens[1],
                            532549.0, 182704.0, "epsg:1234");
           addDocument(writer, newAddress);
        }
    }

    public static FSDirectory createDirectoryContainingSingleDocument(File path)
            throws IOException
    {

        FSDirectory directory = FSDirectory.open(path);
        IndexWriter writer =
                new IndexWriter(directory, iwc);

        Address address =
            new Address(
                "1",
                "Pitney Bowes Mapinfo, 3rd Floor, 186 City Road, London. EC1V 2NT",
                532549.0, 182704.0, "epsg:1234");
        addDocument(writer, address);

        writer.commit();
        writer.close();
        return directory;
    }

    public static FSDirectory createEmptyDirectory(File path)
        throws IOException
    {
        FSDirectory directory = FSDirectory.open(path);
        IndexWriter writer =
                new IndexWriter(directory, iwc);
        writer.commit();
        writer.close();
        return directory;
    }

    private static void addDocument(IndexWriter writer, Address address)
        throws IOException
    {
        Document document = new Document();
        String addressS = address.getAddress();

        FieldType fldType = new FieldType();
        fldType.setIndexed(false);
        fldType.setStored(true);
        fldType.setNumericType(FieldType.NumericType.INT);
        fldType.setNumericPrecisionStep(1);

        document.add(new IntField(SEARCH_ID, Integer.parseInt(address.getId()), fldType));

        fldType = new FieldType();
        fldType.setIndexed(true);
        fldType.setStored(true);
        fldType.setStoreTermVectors(true);
        Field field = new Field(SEARCH_ADDRESS, addressS, fldType);

        document.add(field);
        //document.add(new TextField(SEARCH_ADDRESS, address.getAddress(), Field.Store.YES));

        document.add(new StringField(SEARCH_X, String.valueOf(address.getX()), Field.Store.YES));
        document.add(new StringField(SEARCH_Y, String.valueOf(address.getY()), Field.Store.YES));

        writer.addDocument(document);
    }
    
}
