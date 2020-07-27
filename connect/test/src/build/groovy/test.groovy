
/**
 * Runs a mutation on a specific test suite
 */
def runMutation(mutation, testSuite, testShare, targetDir)
{
    def baseDir = "${targetDir}/${mutation.name}/${testSuite.name}"
    ant.mkdir(dir: baseDir)
    def base = "${testShare}/${mutation.name}/${testSuite.name}"
    def cmd = [project.properties["psexec.executable"], "\\\\${mutation.host}"]
    
    // Use credentials from config file, or failing that, try the maven properties
    // install.user, and install.password. If they don't exist either, don't 
    // provide any credentials, which executes psexec as the user the 
    // current process runs as
    if (mutation.username != null)
    {
        cmd += ["-u", mutation.username, "-p", mutation.password]
    }
    else if (project.properties["install.username"] != null)
    {
        cmd += ["-u", project.properties["install.username"], "-p", project.properties["install.password"]]
    }
    
    cmd += ["java", "-jar", 
            mutation.seleniumJar, 
            "-htmlSuite", mutation.browser, mutation.target, 
            "${testShare}/suite/${testSuite.file}", 
            "${base}/result.html", "-log", 
            "${base}/log.txt"]
    // For debugging only
    def s = ""
    cmd.each{s += " " + "${it}"}
    log.info(s.trim())
    
    def artifactDir = "${project.basedir}/target/reports/${mutation.name}/${testSuite.name}"
    ant.mkdir(dir: artifactDir)
    log.info("Starting psexec...")
    def proc = cmd.execute()
    proc.consumeProcessOutput(System.out, System.err)
    proc.waitForOrKill(testSuite.timeout * 1000)
    def res = proc.waitFor();
    log.info("...done")
    if (res != 0)
    {
        log.info("Psexec failed. The application might have timed out")
    }
    def failed = false
    def repFile = new File("${base}/result.html")
    if (!repFile.exists())
    {
        failed = true
        log.warn("No test result  was found for the suite ${testSuite.name} for variant ${mutation.name}.")
    }
    else
    {
        // The result of psexec is not a reliable indicator of whether the
        // tests passed or not, so we have to parse the HTML report (in a bit 
        // of a nasty way, I admit)
        failed = !repFile.text.replaceAll("\\s*", "").toLowerCase()
                .contains("<td>result:</td><td>passed</td>")
    }

    // copying artifacts
    ant.copy(todir: artifactDir)
    {
        fileset(dir: baseDir) 
        {
            exclude(name: "**/log.txt.lck")
        }
    }
    if (failed)
    {
        log.warn("A test in the suite ${testSuite.name} for variant ${mutation.name} failed.")
    }
    else
    {
        log.info("All tests in the suite ${testSuite.name} for variant ${mutation.name} succeeded.")
    }
    return !failed
}

//export test suite
def testTarget = project.properties["test.target.dir"]
if (testTarget == null)
{
    fail("Property 'test.target.dir' not defined")
}
new File(testTarget).eachDir{ant.delete(dir: "${it}")}

ant.copy(todir: "$testTarget/suite")
{
    fileset(dir: project.basedir)
    {
        exclude(name: "**/.svn/**")
    }
}

// delete previous reports (if any)
ant.delete(dir: "${project.basedir}/target/reports")

def c = project.properties["test.config.file"]
def configFile = null
if (c != null)
{
    configFile = new File(c)
    if (!configFile.absolute)
    {   
        configFile = new File(project.basedir, "$configFile")
    }
    log.info("Using custom config file '$configFile'.")
}
else
{
    configFile = new File(project.basedir, "/test-config.xml")
    log.info("Using default config file '$configFile'")
}
if (!configFile.exists())
{
    fail("Config file '$configFile' not found.")
}

def config = new XmlParser().parse(configFile)
def suites = config."test-suites"."test-suite".collect
{
    [name: it.name.text(), file: it.file.text(), timeout: Integer.parseInt(it.timeout.text())]
}
def testShare = config."test-share".text()
def overallSuccess = true 
def summary = "<html><head></head><body><table cellpadding=\"1\" cellspacing=\"1\" border=\"1\"><tr><th>Suite</th><th>Variant</th><th>Result</th></tr>"
config.mutations.mutation.each
{
    mutation ->
    def m = [name: mutation.name.text(), 
             host: mutation.host.text(), 
             browser: mutation.browser.text(), 
             target: mutation.target.text(), 
             seleniumJar: mutation."selenium-jar".text()]
    if (mutation.username)
    {
        m.username = mutation.username.text();
        m.password = mutation.password.text();
    }
    suites.each
    {
        suite ->
        def res = runMutation(m, suite, testShare, testTarget);
        if (res)
        {
            summary += "<tr><td>$suite.name</td><td>$m.name</td><td>success</td></tr>"
        }
        else
        {
            summary += "<tr><td>$suite.name</td><td>$m.name</td><td style=\"color:red\">failure</td></tr>"
        }
        overallSuccess &= res
    }
}
summary +="</table></body></html>"
def summaryFile = new File("${project.basedir}/target/reports/summary.html")
summaryFile.text = summary

// At this point we can decide if we want to let the build fail. Probably some kind of
// regexp trickery could do the job
if (!overallSuccess)
{
	fail("At least one test failed.")
}
