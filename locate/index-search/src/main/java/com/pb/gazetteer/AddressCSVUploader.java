package com.pb.gazetteer;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;


/**
 * Created by JU002AH on 4/8/2019.
 * This class save uploaded address csv file .
 */
public class AddressCSVUploader {

    private static final Logger log = LogManager.getLogger(AddressCSVUploader.class);
    private static final String CSV_WRITING_MESSAGE = "Error while writing CSV ";
    private static final String CSV_EXTN = ".csv";
    /**
     * Upload and save the csv file data for a gazetteer
     * Maintains only last 3 versions
     *
     * @param configFilePath
     * @param gazetteerName
     * @param inputStream
     */
    public void writeCSVFile(InputStream inputStream, String gazetteerName, String configFilePath) throws IOException {
        File directory = new File(configFilePath);
        if (!directory.exists()) {
            directory.mkdir();
            try {
                write(inputStream, gazetteerName, configFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File csvFile = new File(directory, gazetteerName + ".csv");
            if (csvFile.exists()) {
                File v1csvFile = new File(directory, gazetteerName + ".1.csv");
                if (v1csvFile.exists()) {
                    File v2csvFile = new File(directory, gazetteerName + ".2.csv");
                    if (v2csvFile.exists()) {
                        v2csvFile.delete();
                    }
                    v1csvFile.renameTo(v2csvFile);
                }
                csvFile.renameTo(v1csvFile);
            }
            try {
                write(inputStream, gazetteerName, configFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This writes CSV
     * @param stream
     * @param gazetteerName
     * @param csvFolderPath
     */
    private void write(InputStream stream, String gazetteerName, String csvFolderPath) throws  IOException {
        try {
            File targetFile = new File(csvFolderPath + File.separator + gazetteerName + CSV_EXTN);
            FileUtils.copyInputStreamToFile(stream, targetFile);
        } catch (Exception e) {
            log.error("Error while writing address dataset file into CSV upload", e);
            throw new ServerException(CSV_WRITING_MESSAGE);
        }
    }

}
