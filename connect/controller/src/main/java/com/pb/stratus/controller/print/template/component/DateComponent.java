package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.template.Component;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;


/**
 * This component class would be used to create
 * data-component on the server side.
 * Created by SA021SH on 11/6/2014.
 */
public class DateComponent implements Component {

    private final static Logger logger = LogManager.getLogger(DateComponent.class);
    /**
     * The current supported formats.
     */
    private Collection<String> validFormats = new LinkedHashSet<String>() {{
        add("yyyy-MM-dd");
        add("dd MMMM yyyy");
        add("dd-MM-yy");
        add("dd-MM-yyyy");
        add("dd-MMM-yyyy");
    }};

    private SimpleDateFormat dateFormatter;

    public DateComponent(String format) {
        if(validFormats.contains(format))
        {
            dateFormatter = new SimpleDateFormat(format);
        }
        else
        {
            dateFormatter = new SimpleDateFormat(validFormats.iterator().next());
        }
    }

    @Override
    public void generateSAXEvents(ContentHandler handler) throws SAXException {
        String currentDate = getDate();
        logger.debug(" Date to be printed: " + currentDate);
        handler.characters(currentDate.toCharArray(), 0, currentDate.length() );
    }

    /**
     * Format TODAY as per the desired format.
     * @return
     */
    private String getDate()
    {
        Calendar cal = Calendar.getInstance();
        return dateFormatter.format(cal.getTime());
    }
}
