package com.pb.gazetteer.perftest;


import com.mapinfo.testing.multithreaded.MultithreadedQuery;
import com.mapinfo.testing.multithreaded.MultithreadedResponse;
import com.mapinfo.testing.multithreaded.MultithreadedTestException;
import com.mapinfo.testing.multithreaded.MultithreadedTestRunner;
import com.mapinfo.testing.multithreaded.QueryExecutor;
import com.mapinfo.testing.multithreaded.QueryProducer;
import com.mapinfo.testing.multithreaded.ResponseConsumer;
import com.mapinfo.testing.multithreaded.ThroughputConsumer;
import com.mapinfo.testing.multithreaded.binary.BinaryResponse;
import com.mapinfo.testing.multithreaded.binary.TimingConsumer;
import com.mapinfo.testing.multithreaded.soap.SOAPQuery;
import com.mapinfo.testing.multithreaded.soap.SOAPQueryExecutor;
import com.mapinfo.testing.multithreaded.soap.SOAPQueryProducer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class SoapScalingTest
{

    private static final String CLI_CONFIG_FILE = "configFile";
    private static QName m_serviceName = new QName("http://webservice.gazetteer.pb.com/", "SingleLineAddressService");
    private static QName m_servicePort = new QName("http://webservice.gazetteer.pb.com/", "SingleLineAddressPort");

    public static void main(String[] args)
    {
        Options options = createOptions();
        // create the parser
        CommandLineParser parser = new GnuParser();
        try
        {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            Properties props = new Properties();
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(line.getOptionValue(CLI_CONFIG_FILE)));
            try
            {
                props.load(bis);
            } finally
            {
                IOUtils.closeQuietly(bis);
            }
            runTest(props);
        } catch (ParseException exp)
        {
            System.out.println("Command line parsing failed: " + exp.getMessage());
            printHelp(options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (MultithreadedTestException e)
        {
            System.out.println("Test failed to complete");
            e.printStackTrace();
        }
    }

    public static void runTest(Properties props) throws MultithreadedTestException, FileNotFoundException
    {
        for (TestConfig config : TestConfig.buildConfigs(props))
        {
            List<SOAPQueryProducer.AttachmentInfo> attachmentList;
            if (config.getAttachmentInfo() == null) {
                attachmentList = Collections.emptyList();
            } else {
                attachmentList = Collections.singletonList(config.getAttachmentInfo());
            }

            QueryProducer producer = new SOAPQueryProducer(
                    config.getInputFile(), config.getTemplate(),
                    config.getRecLimit(), config.getLoopCnt(),
                    attachmentList)
            {
                @Override
                protected String escapeDataElement(String element)
                {
                    return StringEscapeUtils.escapeXml(element);
                }
            };

            QueryExecutor executor = new SOAPQueryExecutor(config.getServerUrl(), m_serviceName, m_servicePort, config.isLogErrors(), true, true);

            File resultsFile = new File(config.getOutputDir(), "results_" + config.getThreadCnt() + ".txt");
            PrintStream resultStream = new PrintStream(resultsFile);
            try
            {
                List<ResponseConsumer> consumers = new ArrayList<ResponseConsumer>();
                consumers.add(new TimingConsumer(resultStream));
                consumers.add(new ThroughputConsumer(resultStream));
                if (config.isLogErrors())
                {
                    consumers.add(new ErrorConsumer(config.getOutputDir()));
                }

                MultithreadedTestRunner.runTest(producer, executor,
                        consumers.toArray(new ResponseConsumer[consumers.size()]), config.getThreadCnt(), false);
            } finally
            {
                resultStream.close();
            }
        }
    }

    private static Options createOptions()
    {
        Options result = new Options();
        result.addOption(OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("use given configuration file")
                .isRequired()
                .create(CLI_CONFIG_FILE));
        return result;
    }

    private static void printHelp(Options options)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java " + SoapScalingTest.class.getName(), options, true);
    }

    private static final class TestConfig
    {
        private String m_template;
        private File m_inputFile;
        private URL m_serverUrl;
        private int m_recLimit;
        private int m_threadCnt;
        private int m_loopCnt;
        private File m_outputDir;
        private boolean m_logErrors;
        private SOAPQueryProducer.AttachmentInfo m_attachmentInfo;

        public TestConfig(String template, File inputFile, URL serverUrl, int recLimit, int threadCnt, int loopCnt, File outputDir, boolean logErrors, SOAPQueryProducer.AttachmentInfo attachment)
        {
            m_template = template;
            m_inputFile = inputFile;
            m_serverUrl = serverUrl;
            m_recLimit = recLimit;
            m_threadCnt = threadCnt;
            m_loopCnt = loopCnt;
            m_outputDir = outputDir;
            m_logErrors = logErrors;
            m_attachmentInfo = attachment;
        }

        public String getTemplate()
        {
            return m_template;
        }

        public File getInputFile()
        {
            return m_inputFile;
        }

        public URL getServerUrl()
        {
            return m_serverUrl;
        }

        public int getRecLimit()
        {
            return m_recLimit;
        }

        public int getThreadCnt()
        {
            return m_threadCnt;
        }

        public int getLoopCnt()
        {
            return m_loopCnt;
        }

        public File getOutputDir()
        {
            return m_outputDir;
        }

        public boolean isLogErrors()
        {
            return m_logErrors;
        }

        public SOAPQueryProducer.AttachmentInfo getAttachmentInfo()
        {
            return m_attachmentInfo;
        }

        public static List<TestConfig> buildConfigs(Properties props)
        {
            String templateFile = props.getProperty("template");
            if (templateFile == null)
            {
                throw new IllegalArgumentException("No template file specified in configuration.");
            }

            String template;
            try
            {
                template = FileUtils.readFileToString(new File(templateFile));
            } catch (IOException e)
            {
                throw new IllegalArgumentException("Unable to read template file.", e);
            }

            String csvFile = props.getProperty("inputCsv");
            if (csvFile == null)
            {
                throw new IllegalArgumentException("No input file specified in configuration.");
            }

            File inputFile = new File(csvFile);
            if (!inputFile.exists())
            {
                throw new IllegalArgumentException("Input file not found: " + inputFile.getAbsolutePath());
            }

            int recLimit;
            try
            {
                recLimit = Integer.parseInt(props.getProperty("recLimit", "-1"));
            } catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Invalid record limit: " + props.getProperty("recLimit"), e);
            }

            URL serverUrl;
            try
            {
                serverUrl = new URL(props.getProperty("url"));
            } catch (MalformedURLException e)
            {
                throw new IllegalArgumentException("Invalid url specified", e);
            }

            String threadsStr = props.getProperty("steps.threadCount");
            if (threadsStr == null)
            {
                throw new IllegalArgumentException("No thread steps specified in configuration file.");
            }

            List<Integer> threadSteps = new ArrayList<Integer>();
            try
            {
                String[] threads = threadsStr.split(",");
                for (String thread : threads)
                {
                    threadSteps.add(Integer.parseInt(thread));
                }
            } catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Invalid thread steps specified: " + threadsStr);
            }

            String loopsStr = props.getProperty("steps.inputLoops");
            if (loopsStr == null)
            {
                throw new IllegalArgumentException("No input loop steps specified in configuration file.");
            }

            List<Integer> loopSteps = new ArrayList<Integer>();
            try
            {
                String[] loops = loopsStr.split(",");
                for (String loop : loops)
                {
                    loopSteps.add(Integer.parseInt(loop));
                }
            } catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Invalid loop steps specified: " + loopsStr);
            }

            if (threadSteps.size() != loopSteps.size())
            {
                throw new IllegalArgumentException("Number of steps in thread count does not match the number of steps in loops.");
            }


            File outputDir = new File(props.getProperty("outputDir"));
            if (!outputDir.exists())
            {
                throw new IllegalArgumentException("Non-existent output directory: " + outputDir.getAbsolutePath());
            }
            if (!outputDir.isDirectory())
            {
                throw new IllegalArgumentException("Output directory is not a directory: " + outputDir.getAbsolutePath());
            }

            boolean logErrors = Boolean.parseBoolean(props.getProperty("logErrors"));

            SOAPQueryProducer.AttachmentInfo attachmentInfo = null;
            String cidColProp = props.getProperty("attachmentIDCol");
            if (StringUtils.isNotBlank(cidColProp)) {
                int cidCol;
                try
                {
                    cidCol = Integer.parseInt(cidColProp);
                } catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException("Invalid attachment id column: " + cidColProp, e);
                }

                int pathCol;
                String pathColProp = props.getProperty("attachmentPathCol");
                try
                {
                    pathCol = Integer.parseInt(pathColProp);
                } catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException("Invalid attachment path column: " + pathColProp, e);
                }

                attachmentInfo = new SOAPQueryProducer.AttachmentInfo(cidCol, pathCol);
            }

            List<TestConfig> result = new ArrayList<TestConfig>();
            for (int i = 0; i < threadSteps.size(); i++)
            {
                Integer threadCnt = threadSteps.get(i);
                Integer loopCnt = loopSteps.get(i);
                result.add(new TestConfig(template, inputFile, serverUrl, recLimit, threadCnt, loopCnt, outputDir, logErrors, attachmentInfo));
            }

            return result;
        }
    }

    private static class ErrorConsumer implements ResponseConsumer
    {
        private final Object m_lock = new Object();
        private int m_cnt = 0;
        private File m_outputDir;

        public ErrorConsumer(File outputDir)
        {
            m_outputDir = outputDir;
        }

        public void processOutput(MultithreadedQuery multithreadedQuery, MultithreadedResponse multithreadedResponse) throws MultithreadedTestException
        {
            BinaryResponse response = ((BinaryResponse) multithreadedResponse);
            String responseStr = new String(response.getResult()).toLowerCase();
            if (response.isError() || responseStr.contains("error") || responseStr.contains("exception"))
            {
                int qNum;
                synchronized (m_lock)
                {
                    qNum = m_cnt++;
                }

                try
                {

                    SOAPQuery query = ((SOAPQuery) multithreadedQuery);
                    Source source = new DOMSource(query.getQuery().getSOAPPart());
                    Result result = new StreamResult(new File(m_outputDir, "req_" + qNum + ".xml"));
                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer transformer = factory.newTransformer();
                    transformer.transform(source, result);
                    FileUtils.write(new File(m_outputDir, "res_" + qNum + ".xml"), responseStr);
                } catch (Exception e)
                {
                    throw new MultithreadedTestException("Unable to write request/response files.", e);
                }
            }
        }

        public void processSummary() throws MultithreadedTestException
        {

        }

        public void close() throws MultithreadedTestException
        {

        }
    }
}
