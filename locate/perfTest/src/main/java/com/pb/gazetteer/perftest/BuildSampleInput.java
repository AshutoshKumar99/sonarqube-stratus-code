package com.pb.gazetteer.perftest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.csv.CSVUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BuildSampleInput
{
    private static final String CLI_GAZ_FILE = "inGazFile";
    private static final String CLI_DST_FILE = "outQryFile";
    private static final String CLI_MAX_REC = "maxQueryRec";
    private static final String CLI_NUM_QRY = "numQueries";
    private static final String CLI_GAZ_NAME = "gazNames";
    private static final String CLI_TEN_NAME = "tenNames";

    public static void main(String[] args)
    {
        Options options = createOptions();
        // create the parser
        CommandLineParser parser = new GnuParser();
        try
        {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            run(line);
        } catch (ParseException exp)
        {
            System.out.println("Command line parsing failed: " + exp.getMessage());
            printHelp(options);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void run(CommandLine cl) throws IOException
    {
        Map<String, Integer> distribution = new HashMap<String, Integer>();
        Random rnd = new Random();
        List<String> input = readGazFile(new File(cl.getOptionValue(CLI_GAZ_FILE)));
        int numQry = Integer.parseInt(cl.getOptionValue(CLI_NUM_QRY));
        List<String> gazNames = getGazNames(cl);
        List<String> tenNames = getTenNames(cl);
        String maxRecs = cl.getOptionValue(CLI_MAX_REC);

        BufferedWriter bw = new BufferedWriter(new FileWriter(cl.getOptionValue(CLI_DST_FILE)));
        try
        {
            for (int i = 0; i < numQry; i++)
            {
                String inRec = input.get(rnd.nextInt(input.size()));
                String[] words = inRec.split("\\s+");
                int start = rnd.nextInt(words.length - 3);
                int end = start + rnd.nextInt(3) + 1;
                end = Math.min(end, words.length);
                String searchTerm = StringUtils.join(words, ' ', start, end);
                String gazName = gazNames.get(rnd.nextInt(gazNames.size()));
                String tenName = tenNames.get(rnd.nextInt(tenNames.size()));
                bw.write(CSVUtils.printLine(new String[]{tenName, gazName, maxRecs, searchTerm}, CSVStrategy.EXCEL_STRATEGY));
                bw.newLine();
                addDistribution(distribution, "Tenant: " + tenName + " - Gaz: " + gazName);
            }
        } finally
        {
            IOUtils.closeQuietly(bw);
        }

        outputDistribution(distribution);
    }

    private static void outputDistribution(Map<String, Integer> distribution)
    {
        for (String key : distribution.keySet())
        {
            System.out.println(key + " total count: " + distribution.get(key));
        }
    }

    private static void addDistribution(Map<String, Integer> distribution, String key)
    {
        Integer value = distribution.get(key);
        if (value == null) {
            value = 1;
        } else {
            value = value + 1;
        }

        distribution.put(key, value);
    }

    private static List<String> getTenNames(CommandLine cl)
    {
        return Arrays.asList(cl.getOptionValue(CLI_TEN_NAME).split(","));
    }

    private static List<String> getGazNames(CommandLine cl)
    {
        return Arrays.asList(cl.getOptionValue(CLI_GAZ_NAME).split(","));
    }

    private static List<String> readGazFile(File file) throws IOException
    {
        List<String> result = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        try
        {
            CSVParser parser = new CSVParser(br, CSVStrategy.EXCEL_STRATEGY);
            String[] line;
            while ((line = parser.getLine()) != null)
            {
                result.add(line[0]);
            }
        } finally
        {
            IOUtils.closeQuietly(br);
        }
        return result;
    }

    private static void printHelp(Options options)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java " + BuildSampleInput.class.getName(), options, true);
    }


    private static Options createOptions()
    {
        Options result = new Options();
        result.addOption(OptionBuilder.withArgName("names")
                .hasArg()
                .withDescription("gazetteer names (comma separated)")
                .isRequired()
                .create(CLI_GAZ_NAME));
        result.addOption(OptionBuilder.withArgName("names")
                .hasArg()
                .withDescription("tenant names (comma separated)")
                .isRequired()
                .create(CLI_TEN_NAME));
        result.addOption(OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("input gazetteer file")
                .isRequired()
                .create(CLI_GAZ_FILE));
        result.addOption(OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("output query file")
                .isRequired()
                .create(CLI_DST_FILE));
        result.addOption(OptionBuilder.withArgName("int")
                .hasArg()
                .withDescription("maximum records per query")
                .isRequired()
                .create(CLI_MAX_REC));
        result.addOption(OptionBuilder.withArgName("int")
                .hasArg()
                .withDescription("number of queries")
                .isRequired()
                .create(CLI_NUM_QRY));
        return result;
    }

}
