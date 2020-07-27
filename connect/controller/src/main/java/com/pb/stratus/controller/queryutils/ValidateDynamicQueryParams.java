package com.pb.stratus.controller.queryutils;

import com.pb.stratus.controller.i18n.LocaleResolver;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Validates the input parameters
 * 
 * @author ar009sh
 */

public final class ValidateDynamicQueryParams
{

    /**
     * Validates the input for blank and specific DataType
     * 
     * @param data
     * @param columnType
     * @return
     */
    public static boolean validate(List<String> data, String columnType)
    {
        boolean flag;
        for(String value: data){
            flag = !StringUtils.isBlank(value)
                    && validateSpecficType(value, columnType);
            if(!flag) return flag;
        }
        return true;
    }

    private static boolean validateSpecficType(String value, String columnType)
    {

        boolean result = true;

        try
        {

            if (columnType.equalsIgnoreCase("Character"))
            {

                for (int i = 0; i < value.length(); i++)
                {
                    char c = value.charAt(i);
                    if (!Character.isLetter(c))
                    {
                        throw new NumberFormatException();
                    }
                }

            }
            else if (columnType.equalsIgnoreCase("Integer")
                || columnType.equalsIgnoreCase("Small Integer"))
            {

                Integer.parseInt(value);

            }
            else if (columnType.equalsIgnoreCase("Float"))
            {

                Float.parseFloat(value);

            }
            else if (columnType.equalsIgnoreCase("Short"))
            {

                Short.parseShort(value);

            }
            else if (columnType.equalsIgnoreCase("Date"))
            {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", LocaleResolver.getLocale());
                sdf.setLenient(false);
                sdf.parse(value);

            }
        }
        catch (NumberFormatException exception)
        {
            result = false;

        }
        catch (ParseException e)
        {

            System.out.println("column Type" + columnType + " vlaue " + value);
            result = false;
        }
        catch (IllegalArgumentException e)
        {
            result = false;
        }

        return result;
    }

}
