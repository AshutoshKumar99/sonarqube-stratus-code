package com.pb.stratus.controller.print.template.component;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by SA021SH on 11/6/2014.
 */
public class DateComponentTest {
    private DateComponent dateComponent;

    private ContentHandler mockHandler;

    private String mockFormat = "yyyy-MM-dd";

    @Before
    public void setUp() {
        dateComponent = new DateComponent(mockFormat);
        mockHandler = mock(ContentHandler.class);
    }

    private String getCurrentDate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getDate = DateComponent.class.getDeclaredMethod("getDate");
        getDate.setAccessible(true);
        return (String) getDate.invoke(dateComponent);
    }

    @Test
    public void nullFormatSpecified()
            throws Exception {
        dateComponent = new DateComponent(null);
        dateComponent.generateSAXEvents(mockHandler);
        String currentDate = getCurrentDate();
        verify(mockHandler).characters(currentDate.toCharArray(), 0, currentDate.length());
    }

    @Test
    public void unknownFormatSpecified()
            throws Exception {
        dateComponent = new DateComponent("yyy:MM:dd");
        dateComponent.generateSAXEvents(mockHandler);
        String currentDate = getCurrentDate();

        SimpleDateFormat sdf = new SimpleDateFormat(mockFormat);
        Assert.assertEquals(currentDate, sdf.format(Calendar.getInstance().getTime()));
    }

    @Test
    public void shouldGenerateCorrectDate() throws Exception {
        Collection<String> validFormats = new LinkedHashSet<String>() {{
            add("yyyy-MM-dd");
            add("dd MMMM yyyy");
            add("dd-MM-yy");
            add("dd-MM-yyyy");
            add("dd-MMM-yyyy");
        }};

        Iterator<String> it = validFormats.iterator();

        while (it.hasNext()) {
            String format = it.next();
            dateComponent = new DateComponent(format);
            dateComponent.generateSAXEvents(mockHandler);
            String currentDate = getCurrentDate();
            verify(mockHandler).characters(eq(currentDate.toCharArray()), eq(0),
                    eq(currentDate.length()));

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            System.out.println(" Date : " + currentDate);
            Assert.assertEquals(currentDate, sdf.format(Calendar.getInstance().getTime()));
        }


    }
}
