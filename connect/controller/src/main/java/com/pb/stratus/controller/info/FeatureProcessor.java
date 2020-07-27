package com.pb.stratus.controller.info;

import com.mapinfo.midev.service.featurecollection.v1.*;
import com.mapinfo.midev.service.featurecollection.v1.FeatureCollection;
import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.units.v1.Distance;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.controller.i18n.LocaleResolver;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;

import static java.text.DateFormat.MEDIUM;

/**
 * This is a _Stateless_ utility processing class used to
 * help interpret and convert Midev received features or their attributes
 * into something more meaningful/useful for us to use
 * wherever we need to deal with FeatureCollections.
 * 
 * 
 * @author mo002mi
 *
 */
public class FeatureProcessor
{
	
    private static final Logger logger = LogManager
    .getLogger(FeatureProcessor.class);
    
    public static final String DISTANCE = "Distance";

	  /**
     * gets the array of features from the Midev feature collection based on
     * request parameters specifying which fields are to be returned from the raw
     * feature collection.
     *
     * @param featcols
     * @param summaryFields
     * @param geometry
     * @return
     */
    public static ArrayList<Feature> getFeatures(FeatureCollection featcols,
            List<String> summaryFields, boolean geometry, String tableName)
    {

        AttributeDefinitionList attdef = featcols
                .getFeatureCollectionMetadata().getAttributeDefinitionList();
        

        // get the attribute headers for the indexes
        List<AttributeDefinition> attdeflist = attdef.getAttributeDefinition();

        Map<String, Integer> fieldInfo = new LinkedHashMap<String, Integer>();
        int geometryIndex = -1;
        // the below code gets the column names and there indeces.

        for (int index = 0; index < attdeflist.size(); index++)
        {
            // match a column name to an attribute and index them
            String colname = attdeflist.get(index).getName();

            AttributeDataType type = attdeflist.get(index).getDataType();

            if ((!type.equals(AttributeDataType.STYLE)) &&
            	(!type.equals(AttributeDataType.GEOMETRY)) &&
            	(!type.equals(AttributeDataType.GRID)) &&
            	(!type.equals(AttributeDataType.RASTER)))
            {
                // if fields specified only return them otherwise return all
                // fields.
                if (summaryFields == null || summaryFields.size() == 0)
                {
                    fieldInfo.put(colname, index);
                }
                else if (summaryFields.size() > 0)
                {
                    if (summaryFields.contains(colname) || colname.equals(DISTANCE))
                    {
                        fieldInfo.put(colname, index);
                    }
                }
            }
            // a separate geometry column and only the first geometry column is
            // considered.
            if (geometryIndex < 0 && geometry
                    && type.equals(AttributeDataType.GEOMETRY))
            {
                // there can be more than one geometry column.
                geometryIndex = index;
            }

        }
        
        return compileIndexedFeatures(featcols.getFeatureList(),fieldInfo,geometryIndex, tableName);
    }

    /**
     * Removes features which there distance exceeds the maxium distance set
     * This is due to a bug in envinsa where it returns features which
     * has a distance greater than the set distance
     * @param distance, the set distance for the findNearest search
     * @param col, the featureCollection to extract the feature from
     * @param distanceAttribute, the attribute to define the distance column
     */
    public static void removeFeaturesFromEnvinsaBug(Distance distance,
            com.pb.stratus.controller.info.FeatureCollection col,
            String distanceAttribute)
    {
        if (!isFaultyDistance(distance))
        {
            return;
        }
        double oneMileInMeters = 1609.344;
        List<Feature> features = col.getFeatures();
        for (Iterator<Feature> it = features.iterator(); it.hasNext(); )
        {
            Feature f = it.next();
            String value = (String) f.getProperties().get(distanceAttribute);
            double d = Double.parseDouble(value);
            if (d > (oneMileInMeters * distance.getValue()))
            {
                it.remove();
            }
        }
    }

    /**
     * Checks if the distance set is a faulty distance which the envinsa
     * bug check needs to be applied
     * @param distance, the distance unit to check
     * @return boolean, if the distance needs to be checked against the
     * envinsa bug or not
     */
    private static boolean isFaultyDistance(Distance distance)
    {
        return distance != null && distance.getUom() == DistanceUnit.MILE;
    }
       
    /**
     * This private method is used to compile the list of consumable, indexed
     * features, to be returned by getFeatures. It uses the provided field-index map, against
     * the raw collection of features to produce the requested indexed feature collection containing
     * only the fields requested.
     */
    private static ArrayList<Feature> compileIndexedFeatures(FeatureList featList,
            Map<String, Integer> fieldInfo, int geometryIndex, String tableName)
    {
        ArrayList<Feature> features = new ArrayList<Feature>();
        
        for (int i = 0; i < featList.getFeature().size(); i++)
        {
            Map<String, Object> properties = new LinkedHashMap<String, Object>();
            com.mapinfo.midev.service.featurecollection.v1.Feature feat = featList.getFeature().get(i);
            for (Map.Entry<String, Integer> entry : fieldInfo.entrySet())
            {
                int index = entry.getValue().intValue();
                if (index > -1)
                {
                    String value="";
                    try
                    {
                        value = getAttributeValue(feat, index);
                    }
                    catch (Exception ex)
                    {
                        logger.debug(ex);
                    }
                    properties.put(entry.getKey(), value);

                }
            }

            Geometry featureGeometry = null;
            if (geometryIndex > -1)
            {
                Object att = feat.getAttributeValue().get(geometryIndex);
                if (att instanceof GeometryValue)
                {
                    GeometryValue geom = (GeometryValue) att;
                    featureGeometry = geom.getFeatureGeometry();
                }
                else
                {
                    logger.error("Attribute at index '" + geometryIndex
                            + "' is not a geometry. It will be ignored");
                }
            }

            Feature feature = new Feature(featureGeometry, properties, tableName + "-" + (i + 1));
            features.add(feature);
        }
        
        return features;
    }
        
    /**
     * This method casts the value of the column from the feature to its actual
     * data type.
     *
     * @param feature
     * @param index
     */
    public static String getAttributeValue(
            com.mapinfo.midev.service.featurecollection.v1.Feature feat, int index)
    {
        AttributeValue attr = feat.getAttributeValue().get(index);
        Object value = null;
        try
        {
            if (attr instanceof BooleanValue)
            {
                Method m = BooleanValue.class.getMethod("isValue", null);
                value = m.invoke(attr, null);
            }
            else if (attr instanceof DateValue || attr instanceof DateTimeValue
                    || attr instanceof TimeValue)
            {
                value = getDateString(attr);
            }
            else
            {
                value = PropertyUtils.getProperty(attr, "value");
                
            }
        }
        catch (Exception x)
        {
            // Value stays null
        }
        if (value == null)
        {
            return "";
        }
        else if (value instanceof byte[])
        {
            byte[] encoded = Base64.encodeBase64((byte[]) value);
            try
            {
                return new String(encoded, "UTF-8");
            }
            catch (UnsupportedEncodingException ux)
            {
                // UTF-8 Required by Java Spec
                throw new Error(ux);
            }
        }
        else
        {
            return value.toString();
        }
    }

     /**
     * convert the date value to right localized format
     */
	private static Object getDateString(AttributeValue attr)
    {
        Object value = null;
        Locale local = LocaleResolver.getLocale();
        try
        {
            Object dateValue = PropertyUtils.getProperty(attr, "value");
            if (attr instanceof DateValue)
            {
                DateFormat f = DateFormat.getDateInstance(MEDIUM, local);
                f.setTimeZone(TimeZone.getTimeZone("UTC"));
                value = f.format(getDate(dateValue));
            }
            else if (attr instanceof DateTimeValue)
            {   
                DateFormat f = DateFormat.getDateTimeInstance(MEDIUM, 3, local);
                f.setTimeZone(TimeZone.getTimeZone("UTC"));
                value = f.format(getDate(dateValue));
            }
            else if (attr instanceof TimeValue)
            {
                 DateFormat f = DateFormat.getTimeInstance(2, local);
                 f.setTimeZone(TimeZone.getTimeZone("UTC"));
                 value =  f.format(getDate(dateValue));
            }
        }
        catch(Exception ex)
        {
            // value stays null.
        }
        return value;
    }

    /**
     * Read the date from the envinsa object
     */
    private static Date getDate(Object dateValue)
    {
        XMLGregorianCalendar cal = (XMLGregorianCalendar)dateValue;
        GregorianCalendar gcal = cal.toGregorianCalendar();
        Date date = gcal.getTime();
        return date;
    }
}
