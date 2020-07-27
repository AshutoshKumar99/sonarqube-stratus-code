package com.pb.stratus.controller.util;

/**
 * Utility class for resource/config reads
 *
 */
public class ResourceUtils
{
    
    /**
     * This method ensures that filename does not contain any special
     * characters like \\:?><>*\"|
     * 
     * @param filename
     * @return
     */
    public static boolean isValidFilename(String filename)
    {
        if (null == filename)
        {
            return false;
        }

        if (filename.matches("^.*[\\\\:?><*\"|].*$"))
        {
            return false;
        }

        return true;
    }

}
