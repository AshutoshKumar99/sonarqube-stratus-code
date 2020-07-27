package com.pb.gazetteer.webservice;

import com.pb.gazetteer.*;
import com.pb.gazetteer.lucene.LuceneIndexGenerator;
import com.pb.gazetteer.lucene.LuceneInstance;
import com.sun.xml.ws.developer.StreamingDataHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.transform.TransformerException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;


/**
 * Main class to expose the web service.
 */
@MTOM
@WebService()
public class SingleLineAddress implements AddressServiceIntf
{
    private static final Logger log = LogManager.getLogger(SingleLineAddress.class);
    private final String TENANT_NAME_KEY = "tenant";
    private final String HOST_NAME_KEY = "host";
    private static final String SERVER_MESSAGE =
            "An exception occurred on "
                    + "the server, please try the query again. If it continues to "
                    + "fail, please contact the owner of the application";

    @Resource
    private WebServiceContext m_context;

    @WebMethod()
    public List<Address> search(
            @WebParam(name = "searchParameters") SearchParameters searchParameters)
            throws ServerException, LocateException
    {
        try
        {
            setMDCAttributes(searchParameters.getTenantName());

            log.debug(" Reading Config for : " + searchParameters.getTenantName());
            LocateConfig locateConfig =
                    getProvider().getConfig(searchParameters.getTenantName());

            String gazetteerName = searchParameters.getGazetteerName();
            LocateInstance instance;
            if (StringUtils.isBlank(gazetteerName))
            {
                instance = locateConfig.getDefaultInstance();
            }
            else
            {
                instance = locateConfig.getLocateInstance(gazetteerName);
            }

            return instance.search(searchParameters);
        }
        catch (LocateException x)
        {
            log.error(x);
            throw x;
        }
        catch (Exception x)
        {
            log.error(x);
            throw new ServerException(SERVER_MESSAGE);
        }
        finally{
            removeMDCAttributes();
        }
    }

    @WebMethod()
    public GazetteerNames getGazetteerNames(
            @WebParam(name = "tenantName") String tenantName)
            throws ServerException, LocateException
    {
        try
        {
            setMDCAttributes(tenantName);
            LocateConfig locateConfig = getProvider().getConfig(tenantName);

            List<GazetteerInstance> gazetteerInstances = new ArrayList<GazetteerInstance>();
            for (LocateInstance li : locateConfig.getLocateInstances())
            {
                GazetteerInstance gazetteerInstance=new GazetteerInstance();
                gazetteerInstance.setGazetteerName(li.getName());
                if(li instanceof LuceneInstance)
                {
                    gazetteerInstance.setSrs(((LuceneInstance)li).getSrs());
                }
                gazetteerInstances.add(gazetteerInstance);
            }

            GazetteerNames result = new GazetteerNames();
            result.getGazetteerInstances().addAll(gazetteerInstances);
            result.setDefaultGazetteerName(locateConfig.getDefaultInstance()
                    .getName());
            return result;
        }
        catch (Exception x)
        {
            log.error(x);
            throw new ServerException(SERVER_MESSAGE);
        }finally{
            removeMDCAttributes();
        }
    }

    @WebMethod()
    public PopulateResponse populateGazetteer(
            @WebParam(name = "populateParameters") PopulateParameters populateParameters,
            @XmlMimeType("application/octet-stream") @WebParam(name = "data") DataHandler data)
            throws ServerException
    {
        try
        {
            PopulateResponse response = null;
            setMDCAttributes(populateParameters.getTenantName());
            String configFilePath = getProvider().getLocateConfigPath(
                populateParameters.getTenantName());
            LocateConfig locateConfig =
                    getProvider().getConfig(populateParameters.getTenantName());
            LocateInstance locateInstance =
                    locateConfig.getLocateInstance(populateParameters
                            .getGazetteerName());
            StreamingDataHandler dh = (StreamingDataHandler) data;
            InputStream in = null;
            try
            {
                in = new JaxwsBugFixInputStream(dh.readOnce());
                response  = LuceneIndexGenerator
                .generate((LuceneInstance) locateInstance,
                        populateParameters, in);
                if(response != null && response.getSuccess())
                {
                    //update locate config file 
                    ConfigurationWriter configWriter = new ConfigurationWriter();
                    configWriter.modifyLocateConfigXMLFile(
                        new File(configFilePath), populateParameters);
                }
                else
                {
                    log.info("No row added while generating the index. SRS and SearchLogic is not updated" +
                        " in locate config file.");
                }
                return response;
            }
            finally
            {
                IOUtils.closeQuietly(in);
                try
                {
                    dh.close();
                }
                catch (IOException e)
                {
                    // ignore.. tried to close.
                    log.error("Error while populating Gazetteer populateGazetteer", e.getMessage());
                }
            }
        }
        catch (TransformerException tfe)
        {
            log.error("Error while updating srs in config file", tfe);
            throw new ServerException(SERVER_MESSAGE);
        }
        catch (Exception e)
        {
            log.error("Error building index.", e);
            throw new ServerException(SERVER_MESSAGE);
        }finally{
            removeMDCAttributes();
        }
    }

    private LocateConfigProvider getProvider()
    {
        ServletContext servletContext =
                (ServletContext) m_context.getMessageContext().get(
                        MessageContext.SERVLET_CONTEXT);
        LocateConfigProvider provider =
                (LocateConfigProvider) servletContext
                        .getAttribute(ApplicationContextListener.PROVIDER_KEY);
        if (provider == null)
        {
            log.error("LocateConfigProvider not initialized.");
            throw new ConfigurationException(
                    "LocateConfigProvider not initialized.");
        }

        return provider;
    }

    /**
     * Method to get Hostname to be displayed in locate logs.
     * 
     * @return String hostname.
     */
    private String getHostName()
    {
        String hostName = "";

        try
        {
            InetAddress address = InetAddress.getLocalHost();
            if (null != address)
            {
                hostName = address.getHostName();
            }
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            return "Unknown Host";
        }

        return hostName;
    }

    private void setMDCAttributes(String tenantName)
    {

        String hostName = getHostName();
        if (!StringUtils.isEmpty(tenantName))
        {
            ThreadContext.put(TENANT_NAME_KEY, tenantName);
        }
        if(!StringUtils.isEmpty(hostName))
        {
            ThreadContext.put(HOST_NAME_KEY, hostName);
        }

    }

    private void removeMDCAttributes()
    {
        ThreadContext.remove(TENANT_NAME_KEY);
        ThreadContext.remove(HOST_NAME_KEY);
    }
}
