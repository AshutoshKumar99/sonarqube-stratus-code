/**
 * ------------------------------------------------------------
 * Copyright (c) 2014 Pitney Bowes India Software Pvt. Ltd.
 * All Right Reserved.
 * ------------------------------------------------------------
 *
 * SVN revision information:
 * @version Revision: $Revision$
 * @author Author:   $Author$:
 * @date Date:     $Date$
 */
package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.v1.GetNamedMapLegendsRequest;
import com.mapinfo.midev.service.mapping.v1.GetNamedMapLegendsResponse;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.pb.stratus.controller.MapResolveFailureException;
import com.pb.stratus.core.util.ObjectUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;
import java.util.concurrent.Callable;

public class GetLegendTask implements Callable<OverlayLegend>
{

   private String overlayName;

   private Locale locale;

   private MappingServiceInterface mappingWebService;

   private LegendConverter legendConverter;

   private SecurityContext context;

   private ServletRequestAttributes requestAttributes;

   public GetLegendTask(String overlayName, Locale locale,
                        MappingServiceInterface mappingWebService,
                        LegendConverter legendConverter, SecurityContext context)
   {
      this.overlayName = overlayName;
      this.locale = locale;
      this.mappingWebService = mappingWebService;
      this.legendConverter = legendConverter;
      this.context = context;
      requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes ());
   }

   String getOverlayName()
   {
      return overlayName;
   }

   public OverlayLegend call() throws Exception
   {

      // setting securityContext for thread to call secure mappingService.
      SecurityContextHolder.setContext (context);
      //CONN-14146 fix
      RequestContextHolder.setRequestAttributes (requestAttributes);
      GetNamedMapLegendsRequest request = createRequest ();
      GetNamedMapLegendsResponse response;
      try {
         response = mappingWebService.getNamedMapLegends (request);
      }

      /**
       Basically any type of Exception here should be translated
       MapResolveFailureException, so that one can find out which overlay
       has a problem.

       Catching either Throwable or Error will also catch OutOfMemoryError or InternalError
       from which an application should not attempt to recover.
       Only Exception and its subclasses should be caught.
       **/
      catch (Exception t) {
         MapResolveFailureException ex = new MapResolveFailureException (this.overlayName,
                                                                         t.getMessage ());
         ex.initCause (t);
         throw ex;
      }

      // clear out securityContext after secure mapping service call.
      SecurityContextHolder.clearContext ();
      return convertNamedMapLegendsResponseToLegend (response);
   }

   private GetNamedMapLegendsRequest createRequest()
   {
      GetNamedMapLegendsRequest request = new GetNamedMapLegendsRequest ();
      request.setNamedMap (overlayName);
      return request;
   }

   private OverlayLegend convertNamedMapLegendsResponseToLegend(
                                                                      GetNamedMapLegendsResponse response)
   {
      return legendConverter.convert (overlayName, response);
   }

   public boolean equals(Object o)
   {
      if (o == this) {
         return true;
      }
      if (o == null) {
         return false;
      }
      if (this.getClass () != o.getClass ()) {
         return false;
      }
      GetLegendTask that = (GetLegendTask) o;
      if (!ObjectUtils.equals (this.overlayName, that.overlayName)) {
         return false;
      }
      if (!ObjectUtils.equals (this.locale, that.locale)) {
         return false;
      }
      if (!ObjectUtils.equals (this.mappingWebService, that.mappingWebService)) {
         return false;
      }
      if (!ObjectUtils.equals (this.legendConverter, that.legendConverter)) {
         return false;
      }
      return true;
   }

   public int hashCode()
   {
      int hc = ObjectUtils.SEED;
      hc = ObjectUtils.hash (hc, overlayName);
      hc = ObjectUtils.hash (hc, locale);
      hc = ObjectUtils.hash (hc, mappingWebService);
      hc = ObjectUtils.hash (hc, legendConverter);
      return hc;
   }

}
