package com.pb.stratus.controller.legend;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class LegendItemComparator implements Comparator<LegendItem>
{
    Collator collator;
    
    public LegendItemComparator(Locale locale)
    {
        collator = Collator.getInstance(locale);   
    }
    /*
     * This method will sort the display names of the legend items
     */
    public int compare(LegendItem o1, LegendItem o2)
    {
        return collator.compare(o1.getTitle(), o2.getTitle());
    }
   
}
