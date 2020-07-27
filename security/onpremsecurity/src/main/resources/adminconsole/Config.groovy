final String CONFIG_DIR = "stratus.customer.config.dir";

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://www.changeme.com"
    }

    test {
        /* MANUALLY SET SYSTEM PROPERTIES FOR TESTS */
        System.setProperty(CONFIG_DIR, new File("${basedir}/target/test-classes/customerconfigurations/analyst").getAbsolutePath())
    }
}

/**
 * Tenant name and the configuration directory are taken as argument from VM.
 */
def configDir = System.getProperty(CONFIG_DIR)

println "==================================== CURRENT CONFIGURATION ===================================="
println "customer.configuration.dir = " + configDir
println ""

//configuration directory proprty.
customer.configuration.dir = configDir

//read the configuration file based on the properties given by the command line.
//by convention the properties file will reside in a folder "$configDir"
// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

def PROD_DEBUG_CONFIG = "enableDebugLog"
if(System.getProperty(PROD_DEBUG_CONFIG)) {
    println "Including configuration file specified on command line: " + System.getProperty(PROD_DEBUG_CONFIG);
    grails.config.locations = ["file:$configDir/_global_/adminconsole.properties", "file:$configDir/_global_/shared.properties", "file:$configDir/_global_/adminConsoleLogConfig.groovy" ]
}
else {
    grails.config.locations = ["file:$configDir/_global_/adminconsole.properties", "file:$configDir/_global_/shared.properties"]
}

grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
                     xml: ['text/xml', 'application/xml'],
                     text: 'text-plain',
                     js: 'text/javascript',
                     rss: 'application/rss+xml',
                     atom: 'application/atom+xml',
                     css: 'text/css',
                     csv: 'text/csv',
                     all: '*/*',
                     json: ['application/json', 'text/json'],
                     form: 'application/x-www-form-urlencoded',
                     multipartForm: 'multipart/form-data'
]

// The default codec used to encode data with ${}
grails.views.default.codec = "html" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

log4j = {
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d{[yyyy-MMM-dd HH:mm:ss,SSS]} [tenant:%X{tenant}] [host:%X{host}] [%t] [%c] %x%n [%p] %m%n')
        appender new org.apache.log4j.DailyRollingFileAppender(name: "dailyFile", layout: pattern(conversionPattern: "%d{[yyyy-MMM-dd HH:mm:ss,SSS]} [tenant:%X{tenant}] [host:%X{host}] [%t] [%c] %x%n [%p] %m%n"), file: "${System.getProperty("catalina.home") ?: new File('.').getPath()}/logs/stratus-admin.log", datePattern: "'.'yyyy-MM-dd")
        appender new org.apache.log4j.RollingFileAppender(name: "stacktrace", maxFileSize: 1024,file: "${System.getProperty("catalina.home") ?: new File('.').getPath()}/logs/stratus-admin-stacktrace.log")
    }

    root {
        info 'dailyFile', 'stdout'
    }

    info 'grails.app', 'com.pb.stratus'

    info 'org.apache.commons'

    warn 'org.mortbay.log'

    error 'org.codehaus.groovy.grails',
            'org.springframework',
            'org.hibernate',
            'org.apache.jasper',
            'org.apache.catalina',
            'org.jsecurity',
            'com.opensymphony',
            'grails.spring',
            'grails.util',
            'org.apache.cxf.ws'
}

// All the dimensions are in pixels
branding {
    fmn {
        markerIcon {
            width = 24
            height = 54
            supportedTypes = ["jpeg", "png", "gif", "x-png", "pjpeg"]
        }
        results {
            width = 32
            height = 32
            supportedTypes = ["jpeg", "png", "gif", "x-png", "pjpeg"]
        }
    }
    addressMarkerIcon {
        width = 76
        height = 47
        supportedTypes = ["jpeg", "png", "gif", "x-png", "pjpeg"]
    }
}

// all dimensions in pixels
watermark{
    width = 1024
    height = 1024
    supportedTypes = ["jpg", "jpeg", "png", "gif", "x-png", "pjpeg"]
}