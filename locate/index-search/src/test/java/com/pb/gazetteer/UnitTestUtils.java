package com.pb.gazetteer;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class UnitTestUtils
{
    
    private static List<File> tempDirs = new LinkedList<File>();
    
    static 
    {
        Runnable r = new Runnable()
        {
            public void run()
            {
                for (File dir : tempDirs)
                {
                    if (dir.exists())
                    {
                        try
                        {
                            FileUtils.deleteDirectory(dir);
                        }
                        catch (IOException iox)
                        {
                            System.err.println("Failed to delete " + dir 
                                    + " upon VM shutdown");
                        }
                    }
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(r));
    }
    
    private UnitTestUtils() {}
    
    //XXX move into common location
    public static File createTempDir() throws IOException
    {
        File f = createTempFile();
        f.delete();
        f.mkdir();
        tempDirs.add(f);
        return f.getCanonicalFile();
    }

    public static File createTempFile() throws IOException
    {
        File f = File.createTempFile("test", ".tmp");
        f.deleteOnExit();
        return f;
    }
    
    

}
