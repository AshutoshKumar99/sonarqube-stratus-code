package com.pb.stratus.controller.print;

import com.pb.stratus.core.configuration.ConfigReader;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class TemplateRepository
{
    
    private ConfigReader reader;
    private static final Logger logger = LogManager
            .getLogger(TemplateRepository.class);
    
    public TemplateRepository(ConfigReader reader)
    {
        this.reader = reader;
    }
    
    public Template getTemplate(String name)
    {
        String path = String.format("/printtemplates/%s.fo", name);
        ByteArrayOutputStream bos; 
        try
        {
            InputStream is = reader.getConfigFile(path);
            bos = new ByteArrayOutputStream();
            IOUtils.copy(is, bos);
            is.close();
        }
        catch (IOException iox)
        {
            return null;
        }
        try
        {
            String templateContent = new String(bos.toByteArray(), "UTF-8");
            return new Template(templateContent);
        }
        catch (UnsupportedEncodingException uex)
        {
            //UTF must be supported by JVM
            throw new Error(uex);
        }
    }

    /**
     * Function to parse the .fo template by making the use of DocumentBuilderFactory.
     *
     * @param name
     * @return
     */
    public Document parseFoFile(String name) throws SAXException, IOException, ParserConfigurationException {
        if(name == null || name.isEmpty())
            return null;
        else {
            String path = String.format("/printtemplates/%s.fo", name);

            InputStream inputStream = reader.getConfigFile(path);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");

            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

            return doc;
        }
    }

}
