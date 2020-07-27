package com.pb.stratus.controller.action;

import com.pb.stratus.controller.marker.MarkerFactory;
import com.pb.stratus.controller.marker.MarkerRepository;
import com.pb.stratus.controller.marker.MarkerType;
import com.pb.stratus.controller.print.Marker;
import com.pb.stratus.controller.print.content.FmnResult;
import com.pb.stratus.controller.print.content.FmnResultsCollection;
import com.pb.stratus.controller.print.image.ImageReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static com.pb.stratus.controller.util.ImageAssertUtils.assertPixel;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class FmnResultsCollectionParserTest
{
    
    private FmnResultsCollectionParser parser;
    
    private ImageReader mockImageReader;
    
    private BufferedImage mockImage;
    
    private MarkerRepository mockMarkerRepository;;

    private BufferedImage mockMarkerIcon;

    private MarkerFactory mockMarkerFactory;

    @Before
    public void setUp() throws Exception
    {
        setUpMockMarkerRepository();
        //setUpMarkerFactory();
        mockImageReader = mock(ImageReader.class);
        mockImage = new BufferedImage(1, 2, 
                BufferedImage.TYPE_3BYTE_BGR);
        when(mockImageReader.readFromUrl((URL) any()))
                .thenReturn(mockImage);
        mockMarkerFactory = new MarkerFactory();
        parser = new FmnResultsCollectionParser(mockMarkerFactory, 
                mockMarkerRepository, mockImageReader);
    }
    
    private void setUpMockMarkerRepository() throws Exception
    {
        mockMarkerRepository = mock(MarkerRepository.class);
        mockMarkerIcon = new BufferedImage(12, 34, 
                BufferedImage.TYPE_4BYTE_ABGR);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(mockMarkerIcon, "png", bos);
        bos.toByteArray();
        when(mockMarkerRepository.getMarkerImage(any(String.class), 
                any(MarkerType.class))).thenAnswer(
        new Answer<InputStream>()
        {
            public InputStream answer(InvocationOnMock invocation)
                    throws Throwable
            {
                return new ByteArrayInputStream(bos.toByteArray());
            }
        });
    }
    
    @Test
    public void shouldReturnCorrectNumberOfResults()
    {
        FmnResultsCollection results = parse();
        assertEquals(3, results.getFmnResults().size());
    }
    
    @Test
    public void shouldReturnFmnTitles()
    {
        FmnResultsCollection results = parse();
        FmnResult res = results.getFmnResults().get(0);
        assertEquals("testTitle1", res.getTitle());
        res = results.getFmnResults().get(1);
        assertEquals("testTitle2", res.getTitle());
        res = results.getFmnResults().get(2);
        assertEquals("testTitle3", res.getTitle());
    }

    @Test
    public void shouldReturnMarkersWithCorrectLocation() throws Exception
    {        
        FmnResultsCollection results = parse();
        FmnResult result = results.getFmnResults().get(0);
        assertEquals(new Point(1, 1), result.getMarker().getLocation());
        result = results.getFmnResults().get(1);
        assertEquals(new Point(555000, 500000), 
                result.getMarker().getLocation());
        result = results.getFmnResults().get(2);
        assertEquals(new Point(500000, 500000), 
                result.getMarker().getLocation());
    }
    
    public void shouldRequestCorrectMarkerFromMarkerRepository() 
            throws Exception
    {
        parse();
        verify(mockMarkerRepository, times(3)).getMarkerImage(
                "blue-basket.png", MarkerType.MARKER);
    }
    
    @Test
    public void shouldReturnImagesAsBufferedImages() throws Exception
    {
        BufferedImage mockImage1 = new BufferedImage(1, 2, 
                BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage mockImage2 = new BufferedImage(1, 2, 
                BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage mockImage3 = new BufferedImage(1, 2, 
                BufferedImage.TYPE_3BYTE_BGR);
        mockImageReader = mock(ImageReader.class);
        when(mockImageReader.readFromUrl((URL) any()))
                .thenReturn(mockImage1)
                .thenReturn(mockImage2)
                .thenReturn(mockImage3);
        parser = new FmnResultsCollectionParser(mockMarkerFactory, 
                mockMarkerRepository, mockImageReader);
        FmnResultsCollection results = parse();
        FmnResult res = results.getFmnResults().get(0);
        assertEquals(mockImage1, res.getImage());
        res = results.getFmnResults().get(1);
        assertEquals(mockImage2, res.getImage());
        res = results.getFmnResults().get(2);
        assertEquals(mockImage3, res.getImage());
    }

   

    @Test
    public void shouldPassRightUrlIntoImageReader() throws Exception
    {
        parse();
        verify(mockImageReader).readFromUrl(new URL("file:/test/image1.jpg"));
        verify(mockImageReader).readFromUrl(new URL("file:/test/image2.jpg"));
        verify(mockImageReader).readFromUrl(new URL("file:/test/image3.jpg"));
    }

    @Test
    public void shouldReturnCorrectTitle()
    {
        FmnResultsCollection results = parse();
        assertEquals("testTitle", results.getTitle());
    }

    @Test
    public void shouldIgnoreExceptionsFromRetrievingImages() throws Exception
    {
        when(mockImageReader.readFromUrl((URL) any())).thenAnswer(new Answer<Object>()
        {

            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                URL u = (URL) invocation.getArguments()[0];
                if (u.toString().matches("file:/test/image.*"))
                {
                    throw new IOException();
                }
                return null;
            }
        });
        parse();
    }
    
    @Test
    public void shouldAugmentMarkersWithNumberedIcons()
    {
        mockMarkerFactory = mock(MarkerFactory.class);
        Marker mockMarker = mock(Marker.class);
        Marker mockAugmentedMarker = mock(Marker.class);
        when(mockMarker.augmentWithIcon(any(Point.class), 
                any(BufferedImage.class))).thenReturn(mockAugmentedMarker);
        when(mockMarkerFactory.createMarker(
                any(BufferedImage.class), any(Point.class), 
                any(Point.class))).thenReturn(mockMarker);
        parser = new FmnResultsCollectionParser(mockMarkerFactory, 
                mockMarkerRepository, mockImageReader);
        
        FmnResultsCollection result = parse();

        ArgumentCaptor<BufferedImage> arg = ArgumentCaptor.forClass(
                BufferedImage.class);
        verify(mockMarker, times(3)).augmentWithIcon(eq(new Point(-5, -5)), 
                arg.capture());
        assertEquals(mockAugmentedMarker, 
                result.getFmnResults().get(0).getMarker());
        List<BufferedImage> numberedIcons = arg.getAllValues();
        assertPixel(new double[] {0, 0, 0, 255}, numberedIcons.get(0), 
                11, 14);
        assertPixel(new double[] {255, 255, 255, 255}, numberedIcons.get(0), 
                12, 15);
        //XXX check for other icons as well?
    }

    private FmnResultsCollection parse()
    {
        String json = "{\"fmnresultstitle\":\"testTitle\"," 
            + "\"results\":[{icon:\"blue-basket.png\",x:1.0,y:1.0," 
            + "srs:\"123\",title: \"testTitle1\", " 
            + "image: \"file:/test/image1.jpg\"}, " 
            + "{icon:\"blue-basket.png\",x:555000.0,y:500000.0," 
            + "srs:\"27700\",title: \"testTitle2\", " 
            + "image: \"file:/test/image2.jpg\"}, " 
            + "{icon:\"blue-basket.png\",x:500000.0,y:500000.0," 
            + "srs:\"27700\",title: \"testTitle3\", " 
            + "image: \"file:/test/image3.jpg\"}]}";
        return parser.parse(json);
    }
    
}
