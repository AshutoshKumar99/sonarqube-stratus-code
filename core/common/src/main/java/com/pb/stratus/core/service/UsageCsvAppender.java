package com.pb.stratus.core.service;

import com.pb.stratus.core.configuration.ConfigProperties;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.io.Serializable;
import java.util.zip.Deflater;


/**
 * Created with IntelliJ IDEA.
 * User: VI021CH
 * Date: 23/07/14
 * Time: 12:47
 * This class essentially creates a CSV appender which will internally use a RollingFileAppender
 */
public class UsageCsvAppender {

    public static String LOG_FILE_NAME = "";
    public static String LOG_FILE_NAME_PATTERN = "";
    public static String CURRENT_LOGFILE_PATH = "";
    private static final Logger logger = LogManager.getLogger(UsageCsvAppender.class);
    private static String PATH_TO_CSV = "";


    public static Appender createRollingFileAppender(ConfigProperties configProperties, Configuration config) {
        try {
            PATH_TO_CSV = configProperties.getUsageDataSourceFile();

            /** Check if the PATH_TO_CSV is a valid path, if not no logs will be generated */

            if (checkIfPathExists()) {
                CURRENT_LOGFILE_PATH = PATH_TO_CSV + File.separator;
            } else {
                logger.error("Path specified in usage.data.source.file not valid");
                return null;
            }

            /** LOG_FILE_NAME will vary based on whether it is logging for adminconsole or Connect */
            LOG_FILE_NAME = configProperties.getUsageCSVFileName() + ".csv";
            LOG_FILE_NAME_PATTERN = "${date:yyyy-MM}/" + configProperties.getUsageCSVFileName() + "-%d{dd-MM-yyyy}.csv";

            Layout<? extends Serializable> layout = PatternLayout.createLayout(configProperties.getCsvAppenderPattern(), null, null, null, false, true, null, null);

            final TimeBasedTriggeringPolicy timeBasedTriggeringPolicy = TimeBasedTriggeringPolicy.createPolicy("1", "true");

            String filePattern = CURRENT_LOGFILE_PATH + LOG_FILE_NAME_PATTERN;

            /** Replace has been used to make fwd slashes  as directory separator */
            String filePatternUpdated = filePattern.replace("\\", "/");

            final DefaultRolloverStrategy strategy = DefaultRolloverStrategy.createStrategy(null, "1", null,
                    Deflater.DEFAULT_COMPRESSION + "", config);

            RollingFileAppender appender = RollingFileAppender.createAppender(CURRENT_LOGFILE_PATH + LOG_FILE_NAME, filePatternUpdated, "true",
                    "app-log-file-appender", "false", null, "true", timeBasedTriggeringPolicy, strategy, layout, null, null, null, null, config);
            appender.start();
            config.addAppender(appender);

            return appender;
        } catch (Exception e) {
            logger.error("CSV Appender not able to create log files",e);
            return null;
        }

    }


    public static Boolean checkIfPathExists() {
        if (PATH_TO_CSV != null) {
            File logFilePath = new File(PATH_TO_CSV);
            if (logFilePath.exists()) {
                return true;
            } else
                return false;
        } else
            return false;
    }


}