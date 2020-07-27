/**
 * 
 */
package com.pb.stratus.controller.print.template;

import com.mapinfo.midev.service.geometries.v1.*;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.mapping.v1.MapImage;
import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.annotation.AnnotationStyle;
import com.pb.stratus.controller.annotation.AnnotationType;
import com.pb.stratus.controller.annotation.TextAnnotationStyle;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.MarkerRenderer;
import com.pb.stratus.controller.print.render.AnnotationRendererImpl;
import com.pb.stratus.controller.service.MappingService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author SI001JY
 * 
 */
public class AnnotationRendererImplTest {
	private BufferedImage mockImage;
	private AnnotationRendererImpl annotationsRendererImpl;
	private Dimension imageSize;
	byte[] expectedImageInByte;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.imageSize = new Dimension();
		this.imageSize.setSize(200, 300);
		this.mockImage = new BufferedImage((int) imageSize.getWidth(),
				(int) imageSize.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(mockImage, "jpg", baos);
		baos.flush();
		expectedImageInByte = baos.toByteArray();
		baos.close();
		MapImage mockedMapImage = new MapImage();
		mockedMapImage.setImage(expectedImageInByte);
		RenderMapResponse mockedRenderMapResponse = new RenderMapResponse();
		mockedRenderMapResponse.setMapImage(mockedMapImage);
		/**
		 * Sikhar: Here we are mocking the output of
		 * MappingService.renderMapWithGeometry as our objective is not to test
		 * MappingService but to test AnnotationRendererImpl. The test cases for
		 * MappingService will handle testing for renderMapWithGeometry
		 */
		MappingService mockedMappingService = mock(MappingService.class);
        MarkerRenderer mockMarkerRenderer = mock(MarkerRenderer.class);
		when(
				mockedMappingService.renderMapWithGeometry(imageSize, null,
						null, null)).thenReturn(mockedRenderMapResponse);
		annotationsRendererImpl = new AnnotationRendererImpl(
				mockedMappingService, mockMarkerRenderer);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		mockImage = null;
		annotationsRendererImpl = null;
		imageSize = null;
		expectedImageInByte = null;
	}

	/**
	 * Test method for
	 * {@link AnnotationRendererImpl#renderAnnotations(java.awt.Dimension, com.pb.stratus.controller.print.BoundingBox, java.util.List, java.lang.String)}
	 * .
	 */
	@Test
	public void testRenderAnnotations() {
		BufferedImage actualImage = annotationsRendererImpl.renderAnnotations(
				imageSize, null, new ArrayList<Annotation>(), null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] actualImageByteArray = null;
		try {
			ImageIO.write(actualImage, "jpg", baos);
			baos.flush();
			actualImageByteArray = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(actualImageByteArray);
		assertArrayEquals(expectedImageInByte, actualImageByteArray);
	}

    @Test
    public void testRenderTextAnnotations() {

        Annotation textAnnotation = new Annotation();
        textAnnotation.setType(AnnotationType.TEXT);
        Point pointGeometry = new Point();
        Pos position = new Pos();
        position.setX(1d);
        position.setY(1d);
        pointGeometry.setPos(position);
        textAnnotation.setGeometry(pointGeometry);
        ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        textAnnotation.setStyle(new TextAnnotationStyle(
                "rgb(85, 170, 255)","Ms Gothic","23"));
        textAnnotation.setName("text");
        annotations.add(textAnnotation);
        BufferedImage actualImage = annotationsRendererImpl.renderAnnotations(
                imageSize, new BoundingBox(1,0,2,4,"srs"), annotations, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] actualImageByteArray = null;
        try {
            ImageIO.write(actualImage, "jpg", baos);
            baos.flush();
            actualImageByteArray = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertNotNull(actualImageByteArray);
        assertArrayEquals(expectedImageInByte, actualImageByteArray);
    }

}
