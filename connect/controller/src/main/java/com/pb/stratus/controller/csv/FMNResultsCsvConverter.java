package com.pb.stratus.controller.csv;

import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.info.FeatureCollection;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.*;

/**
 * A CSV Converter for Find my nearest search results. This class writes the distance unit, location and search radius
 * to CSV file.
 * User: GU003DU
 * Date: 9/3/12
 * Time: 2:59 PM
 */
public class FMNResultsCsvConverter extends FeatureCollectionCsvConverter {

    private static final String DISTANCE = "Distance";

    private Float x;
    private Float y;
    private String location;
    private String service;
    private String distanceUnit;
    private String radius;
    private String returnedDistanceUnit;


    /**
     * Constructor for this class.
     *
     * @param featureCollection
     * @param location
     * @param x
     * @param y
     * @param service
     * @param distanceUnit
     * @param returnedDistanceUnit
     * @param radius
     */
    public FMNResultsCsvConverter(FeatureCollection featureCollection, String location, Float x, Float y, String service,
                                  String distanceUnit, String returnedDistanceUnit, String radius) {
        super(featureCollection);
        this.x = x;
        this.y = y;
        this.location = location;
        this.service = service;
        this.distanceUnit = distanceUnit;
        this.returnedDistanceUnit = returnedDistanceUnit;
        this.radius = radius;
    }

    /**
     * Customize the first line and headers.
     *
     * @param separator char CSV field separator
     * @param lineBreak String the line break after each row of the CSV.
     * @return
     */
    @Override
    public String getCsv(char separator, String lineBreak) {

        // add information about search
        String searchInfo = "";
        Object[] values = null;
        if (StringUtils.isBlank(radius)) {
            values = new Object[]{service, location, x, y};
            searchInfo = MessageFormat.format(lookupLocalisation("fmn.search.results.msg1"), values);
        } else {
            values = new Object[]{service, radius, distanceUnit, location, x, y};
            searchInfo =  MessageFormat.format(lookupLocalisation("fmn.search.results.msg2"), values);
        }

        List<String> row = new ArrayList<String>();
        row.add(searchInfo);
        super.addRow(row, separator, lineBreak);

        // Add a blank row
        row.remove(0);
        row.add("");
        super.addRow(row, separator, lineBreak);

        // create the header and other result data.
        return super.getCsv(separator, lineBreak);
    }

    /**
     * This method adds the distance unit to header.
     *
     * @param header
     * @param separator
     * @param lineBreak
     */
    @Override
    protected void addCsvHeader(Set<String> header, char separator, String lineBreak) {

        // Add distance unit to "DISTANCE"; need to create a new Set as the properties map of first feature is backed by "header";
        // any changes to it will change properties of first feature.
        Set<String> properties = new LinkedHashSet<String>();
        properties.addAll(header);
        String distance= lookupLocalisation("fmn.distance");
        if (properties.contains(DISTANCE)) {
            properties.remove(DISTANCE);
            properties.add(distance + " " + "(" + lookupLocalisedDistanceUnit(this.returnedDistanceUnit) + ")");
        }
        List<String> headerList = new ArrayList<String>();
        headerList.addAll(properties);
        super.addRow(headerList, separator, lineBreak);
    }

    /**
     * Lookup the localised text using the given key
     * @param key
     * @return
     */
    private String lookupLocalisation(String key)
    {
        ResourceBundle messages = PropertyResourceBundle.getBundle("legendmessages", LocaleResolver.getLocale());
        try
        {
            return new String(messages.getString(key).getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String lookupLocalisedDistanceUnit(String returnedUnit)
    {

        if(returnedUnit.equalsIgnoreCase("Meter")){
            return lookupLocalisation("Meter");
        }else if(returnedUnit.equalsIgnoreCase("Kilometer")){
            return lookupLocalisation("Kilometer") ;
        } else if(returnedUnit.equalsIgnoreCase("Mile")){
            return lookupLocalisation("Mile");
        } else if(returnedUnit.equalsIgnoreCase("Yard")){
            return lookupLocalisation("Yard");
        } else if(returnedUnit.equalsIgnoreCase("Feet")){
            return lookupLocalisation("Feet");
        }
        return returnedUnit;
    }


}
