package com.pb.stratus.controller.i18n;

import java.util.Locale;

public class LocaleResolver
{
    
    private static ThreadLocal<Locale> locales = new ThreadLocal<Locale>();
    
    public static Locale getLocale()
    {
        return locales.get();
    }
    
    public static void setLocale(Locale locale)
    {
        locales.set(locale);
    }

}
