package com.pb.stratus.controller.infrastructure.cache.legendcache;

import com.pb.stratus.controller.KeyNotInCachePresentException;
import com.pb.stratus.controller.infrastructure.cache.Cacheable;
import com.pb.stratus.controller.legend.OverlayLegend;
import com.pb.stratus.core.common.Preconditions;
import com.pb.stratus.core.configuration.Tenant;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.*;

import static com.pb.stratus.core.common.Constants.LEGEND_CACHE;

/**
 * Author: sh003bh
 * Date: 9/1/11
 * Time: 6:03 PM
 */
public class LegendSerializableCache implements Cacheable
{

    private static final Logger logger = LogManager.getLogger
            (LegendSerializableCache.class.getName());

    private static LegendSerializableCache instance = new
            LegendSerializableCache();

    private LegendSerializableCache()
    {
    }

    public static LegendSerializableCache getInstance()
    {
        return instance;
    }

    @Override
    public void put(Tenant tenant, Serializable key, Serializable value)
    {
        try
        {
            new  LegendCache(tenant).put(key,value);
        } catch (IOException e)
        {
            logger.error("could not write file " + String.valueOf(key));
            logger.error(e.getMessage());
        }
    }

    @Override
    public Serializable get(Tenant tenant, Serializable key)
    {
        Serializable overlayLegend = null;
        try
        {
            overlayLegend = new LegendCache(tenant).get(key);
        } catch (ClassNotFoundException e)
        {
            // log and return null
            logger.error("could no cast serialized object to" + OverlayLegend
                    .class.getName());
            logger.error(e.getMessage());
            return null;
        } catch (IOException e)
        {
            // log and return null
            logger.error("could not read from file " + String.valueOf(key));
            logger.error(e.getMessage());
            return null;
        }
        return overlayLegend;
    }

    @Override
    public Set get(Tenant tenant)
    {
        LegendCache legendCache = new LegendCache(tenant);
        Set<OverlayLegend> overlayLegends = new HashSet<OverlayLegend>();
        List<File> overlayFiles = legendCache.getFileList();
        for (File overlayFile : overlayFiles)
        {
            try
            {
                overlayLegends.add(legendCache.get(overlayFile));
            } catch (ClassNotFoundException e)
            {
                // log and return empty set
                logger.error("could no cast serialized object to" + OverlayLegend
                        .class.getName());
                logger.error(e.getMessage());
                return Collections.emptySet();
            } catch (IOException e)
            {
                // log and return null
                logger.error("could not read  files from tenant " + String.valueOf
                        (tenant.getTenantName()));
                logger.error(e.getMessage());
                return Collections.emptySet();
            }
        }
        return overlayLegends;
    }

    @Override
    public void clear(Tenant tenant) throws KeyNotInCachePresentException
    {
        LegendCache legendCache = new LegendCache(tenant);
        List<File> overlayLegends = legendCache.getFileList();
        for(File overLegend : overlayLegends)
        {
            legendCache.remove(overLegend);
        }
    }

    @Override
    public void clear(Tenant tenant, Serializable key)
            throws KeyNotInCachePresentException
    {
        LegendCache legendCache = new LegendCache(tenant);
        legendCache.remove(key);
    }


    private class LegendCache
    {
        private Tenant tenant;


        public LegendCache(Tenant tenant)
        {
            this.tenant = tenant;
        }

        public void put(Serializable key, Serializable value) throws IOException
        {
            validateKey(key);
            validateValue(value);
            // safe to do casting now
            String overlayName = (String)key;
            OverlayLegend overlayLegend = (OverlayLegend)value;
            LegendCacheSerializationData cacheData =
                    new LegendCacheSerializationData(overlayLegend);
            serializeData(overlayName, cacheData);
        }

        public OverlayLegend get(Serializable key) throws
                ClassNotFoundException, IOException
        {
            validateKey(key);
            String overlayName = (String)key;
            LegendCacheSerializationData cacheData = deserializeData(overlayName);
            if(cacheData == null)
            {
                return null;
            }
            return cacheData.getOverlayLegend();
        }

        public OverlayLegend get(File serializedFile) throws
                ClassNotFoundException, IOException
        {
            Preconditions.checkNotNull(serializedFile, "file cannot be null");
            LegendCacheSerializationData cacheData = deserializeData(serializedFile);
            if(cacheData == null)
            {
                return null;
            }
            return cacheData.getOverlayLegend();
        }

        public void remove(Serializable key)
        {
            validateKey(key);
            String overlayName = (String)key;
            deleteFile(overlayName);
        }

        public void remove(File overlayFile)
        {
            deleteFile(overlayFile);
        }

        private void deleteFile(String overlayName)
        {
            deleteFile(new File(getFilePath(overlayName)));
        }

        private void deleteFile(File f)
        {
            boolean isDeleted  = f.delete();
            if(!isDeleted)
            {
                logger.error("could not delete legend cache file for tenant "
                        + this.tenant.getTenantName() + " having file name "
                        + f.getName());
            }
        }

        private void serializeData(String overlayName,
                LegendCacheSerializationData cacheData) throws IOException
        {
            FileOutputStream fos = null;
            ObjectOutputStream out = null;
            try
            {
                fos = new FileOutputStream(getFilePath(overlayName));
                out = new ObjectOutputStream(fos);
                out.writeObject(cacheData);
            } finally
            {
                if (out != null)
                    out.close();
            }
        }

        private LegendCacheSerializationData deserializeData(String overlayName) throws
                IOException, ClassNotFoundException
        {
            return deserializeData(new File(getFilePath(overlayName)));
        }

        private LegendCacheSerializationData deserializeData(File overlayFile) throws
                IOException, ClassNotFoundException
        {
            if(!overlayFile.exists())
            {
                return null;
            }
            FileInputStream fis = null;
            ObjectInputStream in = null;
            LegendCacheSerializationData cacheData = null;
            try
            {
                fis = new FileInputStream(overlayFile);
                in = new ObjectInputStream(fis);
                cacheData = (LegendCacheSerializationData)in.readObject();
            }
            finally
            {
                if (in != null)
                    in.close();
            }
            return cacheData;
        }

        private List<File> getFileList()
        {
            File file = new File(getFilePath());
            File[] files = file.listFiles();
            List<File> fileList = Arrays.asList(files);
            for(Iterator<File> i = fileList.iterator(); i.hasNext();)
            {
                File f = i.next();
                if(f.isDirectory())
                {
                    i.remove();
                }
            }
            return fileList;
        }

        private String getFilePath()
        {
            return  getFilePath(null);
        }

        private String getFilePath(String overlayName)
        {
            String path =  this.tenant.getTenantPath() + File.separator +
                    LEGEND_CACHE ;
            if(overlayName != null)
            {
                path += File.separator + getUTF8EncodedFilename(overlayName);
            }
            return path;
        }

        /**
         * This is needed because for i18n names, we are unable to retrieve
         * the file.
         * @param fileName
         * @return
         */
        private String getUTF8EncodedFilename(String fileName)
        {
            try
            {
                byte[] utf8Bytes = fileName.getBytes("UTF8");
                String utf8String = new String(utf8Bytes, "UTF8");
                return utf8String;
            } catch (UnsupportedEncodingException e)
            {
                // do nothing minimum UTF-8 should be supported
                e.printStackTrace();
                // return the original filename if unable to convert.
                return fileName;
            }

        }

        private void validateKey(Serializable key)
        {
            if(key == null || !(key instanceof  String))
            {
                throw new IllegalArgumentException("Overlay name is not a " +
                        "String");
            }
        }

        private void validateValue(Serializable value)
        {
            if(value == null || !(value instanceof OverlayLegend))
            {
                 throw new IllegalArgumentException("value should be  " +
                        "instance of OverlayLegend");
            }
        }
    }

}
