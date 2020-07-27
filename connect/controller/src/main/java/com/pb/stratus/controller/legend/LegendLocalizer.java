package com.pb.stratus.controller.legend;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Replaces hard-coded text from an MiDev legend with localised translations.
 */
public class LegendLocalizer
{

    private final static Logger log = LogManager.getLogger(LegendLocalizer.class);
    
    private final static String RANGES_BY = "ranges.by";
    
    private final static String TO = "to.label";
    
    private final static String IND_VALUE = "individual.value";
    
    private final static String RANGE_PATTERN
                                    = "[0-9]*(.|,)[0-9]* to [0-9]*(.|,)[0-9]*";
    
    private final static String MIDEV_RANGE = "Ranges by";
    
    private final static String MIDEV_INDVALUE = "Ind. Value with";

    private Locale locale;

    
    public LegendLocalizer(Locale loc)
    {
        locale = loc;
    }

    public String localiseLabelRangesBy(String label)
    {

        if (label.contains(MIDEV_RANGE)
                                    && label.length()>MIDEV_RANGE.length()+1)
        {
            return constructGenericLabel(label,RANGES_BY);
        }
        else if(label.contains(MIDEV_INDVALUE)
                                    && label.length()>MIDEV_INDVALUE.length()+1)
        {
            return constructGenericLabel(label,IND_VALUE);
        }

        return label;
    }

    public String localiseRange(String layername)
    {

        if(layername.matches(RANGE_PATTERN))
        {
            String[] tokens = layername.split("to");
            if(tokens.length==2)
            {
                layername = localiseNumber(tokens[0].trim()) + " "
                         + this.lookupLocalisation(TO) + " "
                         + localiseNumber(tokens[1].trim());
            }

        }
        return layername;
    }

    private String constructGenericLabel(String label, String key)
    {
        String column = label.split(" ")[label.split(" ").length-1];
        return this.lookupLocalisation(key) + " " + column;
    }

    private String lookupLocalisation(String key)
    {
        ResourceBundle messages = PropertyResourceBundle.
                                        getBundle("legendmessages",getLocale());
        return messages.getString(key);
    }

    private String localiseNumber(String number)
    {
        NumberFormat dformat = DecimalFormat.getNumberInstance(getLocale());
        
        try
        {
            return dformat.format(Float.parseFloat(number));
            
        }catch(NumberFormatException e)
        {
            // do nothing - the "number" is to be returned as it is.
            log.error("Couldn't localise a range number for Legend.",e);
            return number;
        }

        
    }

    /**
     * @return the locale
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }


}
