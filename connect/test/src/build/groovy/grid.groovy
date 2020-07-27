import com.thoughtworks.selenium.*;
import org.xml.sax.*;
import groovy.xml.MarkupBuilder;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Task implements Runnable{
    def config
    def browser
    def url
    def suite
    def results
    def browserResults
    def overall
    def log
    def project

    public Task(config, browser, url, suite, results, browserResults, overall, project, log){
	this.config = config
	this.browser = browser
	this.url = url
	this.suite = suite
	this.results = results
        this.browserResults = browserResults
	this.overall = overall
	this.project = project
        this.log = log
    }
    public void run(){
        log.info("Creating HTTPCommandProcessor for host $config.hostName, port $config.port, browser $browser, URL $url")
	def cp = new HttpCommandProcessor(config.hostName, config.port, 
		    browser, url)
	cp.start()
	try
	    {		
		log.info("Executing $suite on $browser against $url")
		def check=executeSuiteWithCommandProcessor(suite, cp, browserResults, overall)
		if (!check) results.pass=false;
	    }
	finally
	{
	    cp.stop();
	}	    	
    }

    /**
 * Executes a test suite with the given command processor
 */
def executeSuiteWithCommandProcessor(suiteFile, cp, results, overall)
{
    results.cases=[:]
    results.pass=true
    results.failCount=0;
    //FIXME create entity resolver that returns XHTML DTD, otherwise it 
    //      requests it from w3c every time, which is slooooooooow
    def parser = new XmlParser(false, false)
    parser.entityResolver = createEntityResolver();
    def html
    try
    {
        html = parser.parse(suiteFile)
    }
    catch (FileNotFoundException fnfx)
    {
        log.warn("Test suite ${suiteFile} doesn't exist. Ignored")
        return;
    }
    catch (SAXParseException spx)
    {
        log.warn("Test suite ${suiteFile} isn't well-formed. Ignored")
        return;
    }
    html.body[0].table[0].tbody[0].tr.each
    {
        row ->
        if (row.td[0].a)
        {
            def testName = row.td[0].a[0].text()
            def testFile = row.td[0].a[0]."@href"
            try
            {
                results.cases[testName] = [:]
                results.cases[testName].testFile = testFile
                executeTest(new File(suiteFile.parentFile, row.td[0].a[0]."@href"), cp, results.cases[testName], overall)
            }
            catch (FileNotFoundException fnfx)
            {
                log.warn("Test file '$testFile' not found. It will be ignored.")
            }
            catch (SAXParseException spx)
            {
                results.pass=false
                results.failCount++
                log.error("Test '${testName}' failed: " + spx.message)
            }
            catch (SeleniumException x)
            {
                results.pass=false
                results.failCount++
                log.error("Test '${testName}' failed: " + x.message)
            }
        }
    }
    return results.pass;
}

/**
 * Executes a single test file with the given command processor
 */
def executeTest(testFile, cp, results, overall)
{
    results.commands = []
    results.pass=true
    def parser = new XmlParser(false, false)
    parser.entityResolver = createEntityResolver();
    def html = parser.parse(testFile)
    html.body[0].table[0].tbody[0].tr.each
    {
        row ->
        def cmd = row.td[0].text()
        def args = row.td[1..row.td.size() - 1].collect{it.text()}
        //FIXME make this a bit more extensible (Strategy pattern, etc.)
        if (cmd.trim() == "store")
        {
            cmd = "storeExpression"
        }
        else if (cmd.trim() == "echo")
        {
            def resultMessage;
            try {

                def defSel = new DefaultSelenium(cp)
                //def resultEval = defSel.getEval(args[0])
                def resultExpression = defSel.getExpression(args[0])
    
                try {
                    def resultEval= defSel.getEval(args[0])
                    if (resultEval!=resultExpression)
                        resultExpression+=" ("+resultEval+")"
                }
                catch(Exception ex)
                {}
                resultMessage=resultExpression;
             }

             catch(Exception ex)
             {
                 resultMessage="Failed";
             }

            results.commands += [success: true, command: "echo [${args[0]}]", msg: resultMessage]
            return;
        }
        else if (cmd.trim() == "pause")
        {
            def time = Integer.parseInt(args[0])
            try
            {
                Thread.sleep(time)
                // return from the closure, we don't want to call doCommand in
                // this case
                results.commands += [success: true, command: "pause [$time]", msg: "Success"]
                return;
            }
            catch (InterruptedException ix)
            {
                results.commands += [success: true, command: "pause [$time]", msg: "Interrupted prematurely"]
                // just continue if sleep was interrupted
            }
        }
        try
        {
            def s=cp.doCommand(cmd, args as String[])

            if (cmd.startsWith("store"))
            {
                try
                {
                    def defSel = new DefaultSelenium(cp)
                    def resultExpression = defSel.getExpression("\${"+args[1]+"}")
                    if (args[0]!=resultExpression) s=" = " + resultExpression
                }
                catch(Exception ex)
                {
                }

            }

            results.commands += [success: true, command: "$cmd $args", msg: s]
        }
        catch (SeleniumException sx)
        {
            results.commands += [success: false, command: "$cmd $args", msg: sx.message]
            overall.failed = true
            results.pass = false
            throw sx
        }
        return results.pass
    }
   }

   def createEntityResolver()
{
    def dtd = new File("${project.basedir}/src/build/groovy/xhtml1-strict.dtd");
    def s = new InputSource()
    s.characterStream = dtd.newReader("UTF-8")
    s.encoding = "UTF-8"
    s.publicId = "-//W3C//DTD XHTML 1.0 Strict//EN"
    s.systemId = dtd.toURL().toString()
    return new XHTMLEntityResolver(s);
}
}
class XHTMLEntityResolver implements EntityResolver
{
    
    private InputSource s
    
    XHTMLEntityResolver(InputSource s)
    {
        this.s = s;
    }

    InputSource resolveEntity(String publicId, String systemId)
    {
        if (publicId == s.publicId)
        {
            return s;
        }
        else
        {
            return null
        }
    }
}



/**
 * Executes the given suite against all configured hosts and browsers
 */
def executeSuite(suite, config, results, overall, tpe, tasks)
{

    results.pass=true
    targetHosts.split(",").each
    {
        host ->
        def url = "http://$host/"
        def hostResults = [:]
        results.hosts=[:]
        results.hosts[host] = hostResults
	int i = 0
        browsers.split(",").each
        {
                
	        browser ->
		def browserResults = [:]
		hostResults[browser] = browserResults		
		
		//Queue and run from a thread pool so that we can parallelise tests
		tasks[i] = new Task(config, browser, url, suite, results, browserResults, overall, project, log);
		tpe.execute(tasks[i]);
		i++;
        }
    }

}


// A singleton entity resolver
//def entityResolver = new XHTMLEntityResolver(project.basedir);

def config = {}
// set some defaults
config.hostName = project.properties["selenium.host"]
if (!config.hostName)
{
    config.hostName = "localhost"
}

config.port = project.properties["selenium.port"]
if (config.port)
{
    config.port = Integer.parseInt(config.port)
}
if (!config.port)
{
    config.port = 4444;
}

config.browsers = project.properties["selenium.browsers"]
if (!config.browsers)
{
    config.browsers = "*chrome"
}

// A target host is something completely different to the hostName above. It's
// a host that is used to construct a URL that the test browser will be 
// pointed to. The hostName is the name of the machine that runs Selenium Grid
// A target host can also carry an optional port number (e.g. localhost:8080)
config.targetHosts = project.properties["selenium.target.hosts"]
if (!config.targetHosts)
{
    config.targetHosts = "localhost:8080"
}

// Find all files called suite.html in the src/test/selenese directory. They are
// the test suites we're interested in.
//XXX implement a comma-saparated list of file names that will be interpreted
//    as the suites to be ignored.

def singleSuite = project.properties["selenium.suite"]
def suites;
if (singleSuite)
{
    suites = [new File("${project.basedir}", singleSuite)]
}
else
{
    suites = ant.fileScanner 
    {
        //XXX change that to .../src/test/selenese
        fileset(dir: "${project.basedir}")
        {
            include(name: "**/suite.html")
        }
    }
}

def pause = System.getProperty('pause')
if (pause != null) 
{
    log.info("Pausing for ${pause} seconds")
    Thread.sleep(Integer.parseInt(pause) * 1000);
}

def results = [:]
// add Subversion revision number
results.revision = System.getProperty("build.vcs.number.1")
results.testBuildNumber=System.getProperty("build.number");

//results.revision = "(coming soon)"
results.suites = [:]
// iterate over all suites
def exception;
try
{
   int nTasks = 100;//Need to remove this hardcoded value and use "fileset.size()" instead
   long n = 1000L;
   //set tpSize to 2 for now to limit threads created until we work out what is best to do for scaling Selenium Grid.
   int tpSize = 2;//Need to remove this hardcoded value and use "fileset.size()" instead 
   ThreadPoolExecutor tpe = new ThreadPoolExecutor(tpSize, tpSize, 50000L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());
   Task[] tasks = new Task[nTasks];    

   for (s in suites)
    {
        def suiteName = s.canonicalPath
        results.suites[suiteName] = [:]
        executeSuite(s, config, results.suites[suiteName], results, tpe, tasks)
    }
    tpe.shutdown();
    tpe.awaitTermination(10000000L, TimeUnit.SECONDS);

}
catch (Exception x)
{
    // store exception for later
    exception = x;
}

StringWriter w = new StringWriter()
def res = new MarkupBuilder(w)
res.results
{
    "svn-revision-number"(results.revision)
    "test-build-number"(results.testBuildNumber)
    "base-dir"(new File("${project.basedir}").canonicalPath)
    results.suites.each
    {
        suiteName, s ->
        suite(suiteName: suiteName,
              pass:s.pass)
        {
            s.hosts.each
            {
                hostName, h ->
                host(hostName: hostName)
                {
                    h.each
                    {
                        browserName, b ->
                        browser(browserName: browserName,
                                pass:b.pass,
                                "fail-count":b.failCount)
                        {
                            b.cases.each
                            {
                                testName, tst ->
                                testcase(testName: "$testName (${tst.testFile})",
                                         pass:tst.pass)
                                         
                                {
                                    tst.commands.each
                                    {
                                        cmd ->
                                        command
                                        {
                                            signature(cmd.command)
                                            success(cmd.success)
                                            message(cmd.msg ? cmd.msg : "")
                                        }
                                    }
                                }
                            }
                        } }
                }
            }
        }
    }            
}

ant.mkdir(dir: "${project.basedir}/target/acceptance-tests")
def r = new File("${project.basedir}/target/acceptance-tests/report.xml")
r.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n${w.toString()}", "UTF-8")


def t = TransformerFactory.newInstance().newTransformer(new StreamSource(
        new File("${project.basedir}/src/build/groovy/summary.xslt")));
t.transform(new StreamSource(
        new File("${project.basedir}/target/acceptance-tests/report.xml")), 
    new StreamResult(
        new File("${project.basedir}/target/acceptance-tests/report.html")));


def u = TransformerFactory.newInstance().newTransformer(new StreamSource(
        new File("${project.basedir}/src/build/groovy/details.xslt")));
    u.transform(new StreamSource(
        new File("${project.basedir}/target/acceptance-tests/report.xml")), 
        new StreamResult(
        new File("${project.basedir}/target/acceptance-tests/detailed-report.html")));
/*
ant.xslt('in': "${project.basedir}/target/acceptance-tests/report.xml", 
         out: "${project.basedir}/target/acceptance-tests/report.html",
         style: "${project.basedir}/src/build/groovy/report.xslt");
*/
if (exception)
{
    log.error("", exception)
    fail("An error occurred during processing the tests")
}
else if (results.failed)
{
    fail("At least one test has failed. Please check report.html for more details")
}

