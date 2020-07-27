

package com.pb.stratus.controller.print.render;

import com.mapinfo.midev.service.geometries.v1.*;
import com.mapinfo.midev.service.geometries.v1.Polygon;
import com.mapinfo.midev.service.mapping.v1.MapImage;
import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.mapinfo.midev.service.style.v1.MapBasicAreaStyle;
import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.annotation.AnnotationStyle;
import com.pb.stratus.controller.annotation.AnnotationType;
import com.pb.stratus.controller.annotation.TextAnnotationStyle;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.Marker;
import com.pb.stratus.controller.print.MarkerRenderer;
import com.pb.stratus.controller.service.MappingService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.graphdata.utilities.resource.ServletContextResourceResolver;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnnotationRendererImpl implements AnnotationRenderer {
    private static final Logger logger = LogManager
            .getLogger(AnnotationRendererImpl.class);
    private MappingService mappingService;
    private MarkerRenderer markerRenderer;
    private String MARKER_ICON_PATH = "/stratus/img/marker-default.png";

    public AnnotationRendererImpl(MappingService mappingService, MarkerRenderer markerRenderer) {
        this.mappingService = mappingService;
        this.markerRenderer = markerRenderer;
    }

    @Override
    public BufferedImage renderAnnotations(
            Dimension imageSize, BoundingBox boundingBox, List<Annotation> annotations,
            String displayUnit) {
        BufferedImage annotationLayer = new BufferedImage(
                imageSize.width, imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        if(annotations.size() == 0)
            return annotationLayer;


        AnnotationsRendererHelperBean ssAnnotations = new AnnotationsRendererHelperBean();
        Annotation newAnnotation =null;
        List<Annotation> additionalNewAnnotations = new ArrayList<>();
        for(Annotation annotation : annotations)
        {
            if(annotation.getGeometry() instanceof com.mapinfo.midev.service.geometries.v1.Polygon ||
                    annotation.getGeometry() instanceof MultiPolygon ||
                    annotation.getGeometry() instanceof Curve )
            {
                AnnotationStyle style = annotation.getStyle();
                //hack for handling border opacity for polygons,
                //create clone of polygon with solid fill as a no fill and add to annotations,as we are not applying opacity
                //on hollow fills.
                if(style.getSpatialGraphicCode() =="2")
                {
                    newAnnotation = new Annotation();
                    AnnotationStyle newStyle = new AnnotationStyle(style.getFillColor(),0.5d,style.getStrokeColor(),
                            style.getStrokeWidth(),style.getStrokePattern());//
                    newAnnotation.setStyle(newStyle);
                    newStyle.setSpatialGraphicCode("1");
                    newAnnotation.setType(annotation.getType());
                    newAnnotation.setName(annotation.getName()+"clone");
                    newAnnotation.setGeometry(annotation.getGeometry());
                    additionalNewAnnotations.add(newAnnotation);
                }

            }

        }
        if(additionalNewAnnotations.size()>0)
            annotations.addAll(additionalNewAnnotations);

        // get ss annotations for printing except point and text.
        getAnnotations(annotations,ssAnnotations);

        List<Annotation> solidFillPolygonAnnotations = ssAnnotations.getSolidFillPolygonAnnotations();
        List<Annotation> lineAnnotations = ssAnnotations.getLineAnnotations();
        List<Annotation> nonSolidFillPolygonAnnotations = ssAnnotations.getNonSolidFillPolygonAnnotations();
        List<Annotation> pointAnnotations = getAnnotationWithType(annotations, AnnotationType.POINT);
        List<Annotation> textAnnotations = getAnnotationWithType(annotations, AnnotationType.TEXT);

        try {
            if(solidFillPolygonAnnotations.size()> 0)
            {
                BufferedImage polygons = renderSSAnnotations(imageSize, boundingBox, solidFillPolygonAnnotations, displayUnit);

                float opacity = Float.parseFloat("0.50");
                Graphics2D graphicsCanvas = (Graphics2D) annotationLayer.getGraphics();
                graphicsCanvas.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, opacity));
                graphicsCanvas.drawImage(polygons, null, 0, 0);
                graphicsCanvas.dispose();
            }

            if(nonSolidFillPolygonAnnotations.size() > 0)
            {
                BufferedImage nonSolidPolygons = renderSSAnnotations(imageSize, boundingBox, nonSolidFillPolygonAnnotations, displayUnit);
                annotationLayer.createGraphics().drawImage(nonSolidPolygons, null, 0, 0);
            }

            if(lineAnnotations.size() > 0)
            {
                BufferedImage lines = renderSSAnnotations(imageSize, boundingBox, lineAnnotations, displayUnit);
                annotationLayer.createGraphics().drawImage(lines, null, 0, 0);
            }

            if(pointAnnotations.size() > 0){
                BufferedImage points = renderSSAnnotations(imageSize, boundingBox, pointAnnotations, displayUnit);
                annotationLayer.createGraphics().drawImage(points, null, 0, 0);
            }

            if(textAnnotations.size() > 0){
                TextAnnotationRenderer renderer = new TextAnnotationRenderer();
                BufferedImage textAnnotationLayer = renderer.renderTextAnnotations(imageSize, boundingBox, textAnnotations);
                annotationLayer.createGraphics().drawImage(textAnnotationLayer, null, 0, 0);
            }
        } catch (Exception e) {
            logger.warn(
                    "Exception while requesting the map image for Geometries ",
                    e);
        }
        return annotationLayer;
    }

    private List<Marker> getAnnotationMarkers(List<Annotation> annotations) {
        List<Marker> markers = new ArrayList<Marker>();
        HttpServletRequest curRequest = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        ServletContextResourceResolver resourceResolver = new ServletContextResourceResolver(curRequest.getSession().getServletContext());
        try {
            BufferedImage icon = ImageIO.read(resourceResolver.getResourceAsStream(MARKER_ICON_PATH));
            Point anchorPoint = new Point((int) (icon.getWidth() / 2d),
                    icon.getHeight() - 1);

            for(Annotation annotation : annotations)
            {
                Point location = new Point();
                Pos position = ((com.mapinfo.midev.service.geometries.v1.Point)annotation.getGeometry()).getPos();
                location.setLocation(position.getX(), position.getY());
                Marker marker = new Marker(icon, anchorPoint, location);
                markers.add(marker);
            }
        } catch (IOException e) {
            logger.error("Unable to load point annotation marker icon.");
        }
        return markers;
    }


    private List<Annotation> getAnnotationWithType(List<Annotation> annotations, AnnotationType type) {
        List<Annotation> result = new ArrayList<Annotation>();
        for(Annotation annotation : annotations){
            if(annotation.getType().equals(type)){
                result.add(annotation);
            }
        }
        return result;
    }

    private AnnotationsRendererHelperBean getAnnotations(List<Annotation> annotations,AnnotationsRendererHelperBean styledAnnotations) {
        List<Annotation> solidFillPolygons = new ArrayList<>();
        List<Annotation> nonSolidFillPolygons = new ArrayList<>();
        List<Annotation> lineAnnotations = new ArrayList<>();

        for(Annotation annotation : annotations){
            if (annotation.getGeometry() instanceof Polygon ||
                    annotation.getGeometry() instanceof MultiPolygon ||
                    annotation.getGeometry() instanceof Curve ) {
                if(annotation.getStyle().getSpatialGraphicCode()=="2")
                {
                   solidFillPolygons.add(annotation);
                }
                else
                {
                   nonSolidFillPolygons.add(annotation);
                }

            }
            if (annotation.getGeometry() instanceof LineString)
            {
                 lineAnnotations.add(annotation);
            }
        }

        styledAnnotations.setSolidFillPolygonAnnotations(solidFillPolygons);
        styledAnnotations.setNonSolidFillPolygonAnnotations(nonSolidFillPolygons);
        styledAnnotations.setLineAnnotations(lineAnnotations);

        return  styledAnnotations;
    }

    private BufferedImage renderSSAnnotations(Dimension imageSize, BoundingBox boundingBox, List<Annotation> annotations,
                                              String displayUnit) throws Exception
    {
        RenderMapResponse mapResponse = mappingService
                .renderMapWithGeometry(imageSize, boundingBox, annotations, displayUnit);
        MapImage mapImage = mapResponse.getMapImage();
        return getBufferedImageFromMapImage(mapImage);
    }

    /**
     * This requires us to pass in the requested image size, as we don't get
     * this info back as part of the RenderNamedMapResponse as far as I can see.
     */
    private BufferedImage getBufferedImageFromMapImage(MapImage mapImage)
            throws IOException {
        BufferedImage buffImage = ImageIO.read(new ByteArrayInputStream(
                mapImage.getImage()));
        return buffImage;
    }

    class TextAnnotationRenderer{

        public BufferedImage renderTextAnnotations(Dimension imageSize, BoundingBox boundingBox, List<Annotation> annotations){

            BufferedImage canvas = new BufferedImage(imageSize.width,
                    imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = canvas.createGraphics();
            String[] colors =null;
            Color color= null;
            Font font =null;

            try{
                for(Annotation annotation: annotations){
                    if(annotation.getType().equals(AnnotationType.TEXT)){
                        //CONN-21343
                        AnnotationStyle annotationStyle = annotation.getStyle();
                        String fontFamily = ((TextAnnotationStyle) annotationStyle).getFontFamily();
                        String fontColor = annotationStyle.getStrokeColor();
                        String fontSize = annotationStyle.getStrokeWidth();
                        font = new Font(fontFamily, Font.BOLD, Integer.valueOf(fontSize));
                        if(fontColor.contains("#"))
                        {
                        color =Color.decode(fontColor);
                        }
                        else
                        {
                        colors = fontColor.substring(3).replaceAll("[()\\s+]", "").split("[,]");
                        color = new Color(Integer.valueOf(colors[0]), Integer.valueOf(colors[1]), Integer.valueOf(colors[2]));
                        }
                        Pos position = ((com.mapinfo.midev.service.geometries.v1.Point)annotation.getGeometry()).getPos();
                        g.setFont(font);
                        g.setColor(color);
                        drawText(g, boundingBox, imageSize, annotation.getName(), position);
                    }
                }

            }
            finally {
                g.dispose();
                colors=null;
            }
            return canvas;
        }

        private void drawText(Graphics2D g, BoundingBox boundingBox,Dimension imageSize, String text, Pos position){
            Point bottomLeft = calculateOffsetFromBottomLeft(boundingBox, imageSize, position);
            FontMetrics fm =  g.getFontMetrics();
            int left = bottomLeft.x - (fm.stringWidth(text)/2);
            int top = (imageSize.height - bottomLeft.y - 1) + (fm.getAscent()/2);
            g.drawString(text, left, top);
        }

        private Point calculateOffsetFromBottomLeft(BoundingBox boundingBox,
                                                    Dimension imageSize, Pos position)
        {
            double offsetX = (position.getX() - boundingBox.getWest())
                    / (boundingBox.getEast() - boundingBox.getWest())
                    * imageSize.width;
            double offsetY = (position.getY() - boundingBox.getSouth())
                    / (boundingBox.getNorth() - boundingBox.getSouth())
                    * imageSize.height;
            return new Point((int) offsetX, (int) offsetY);
        }


    }

}
