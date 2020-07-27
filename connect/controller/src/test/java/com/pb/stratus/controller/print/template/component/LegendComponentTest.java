package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.legend.*;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.template.XslFoUtils;
import com.pb.stratus.controller.util.MockContentHandlerWatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LegendComponentTest
{
    private ContentHandler mockContentHandler;

    private LegendComponent component;

    private MockContentHandlerWatcher watcher;

    private LegendData mockLegendData;

    private Locale oldLocale;

    private MapConfig mockMapConfig;

    private final String OVERLAY1_NAME = "overlay1";
    private final String OVERLAY2_NAME = "overlay2";
    private final String OVERLAY1_FRIENDLY_NAME = "Friendly Map1";
    private final String OVERLAY2_FRIENDLY_NAME = "Friendly Map2";
    private final String OVERLAY1_ITEM1 = "item1overlay1";
    private final String OVERLAY2_ITEM1 = "item1overlay2";
    private final String OVERLAY2_ITEM2 = "item2overlay2";
    private final String OVERLAY2_RANGE1 = "range11overlay2";
    private final String OVERLAY2_RANGE2 = "range21overlay2";

    @Before
    public void setUp() throws IOException
    {
        setUpMockLegendData();
        setUpMapConfigRepository();
        component = new LegendComponent("someTitle", mockMapConfig,
                mockLegendData);
        watcher = new MockContentHandlerWatcher();
        watcher.useNamespace(XslFoUtils.NS_URI);
        watcher.usePrefix("fo");
        mockContentHandler = watcher.getMock();
        oldLocale = LocaleResolver.getLocale();
        LocaleResolver.setLocale(new Locale("en"));
    }

    @After
    public void tearDown()
    {
        LocaleResolver.setLocale(oldLocale);
    }

    private void setUpMapConfigRepository()
    {
        mockMapConfig = mock(MapConfig.class);
        MapConfig.MapDefinition mockDefinition1 =
                mock(MapConfig.MapDefinition.class);
        MapConfig.MapDefinition mockDefinition2 =
                mock(MapConfig.MapDefinition.class);
        List mockDefinitions = new ArrayList();
        mockDefinitions.add(mockDefinition1);
        mockDefinitions.add(mockDefinition2);
        mockMapConfig.setMapDefinitions(mockDefinitions);
        when(mockMapConfig.getMapDefinitionByMapNameOrNull(OVERLAY1_NAME))
            .thenReturn(mockDefinition1);
        when(mockMapConfig.getMapDefinitionByMapNameOrNull(OVERLAY2_NAME))
            .thenReturn(mockDefinition2);
        when(mockDefinition1.getFriendlyName()).thenReturn(
            OVERLAY1_FRIENDLY_NAME);
        when(mockDefinition2.getFriendlyName()).thenReturn(
            OVERLAY2_FRIENDLY_NAME);
    }

    private void setUpMockLegendData()
    {
        LegendItem item1 = createLegendItem(OVERLAY1_ITEM1);
        OverlayLegend l1 = new OverlayLegend(OVERLAY1_NAME,
                Arrays.asList(item1));
        LegendItem item2 = createLegendItem(OVERLAY2_ITEM1);
        LegendItem item3 = new ThematicLegendItem(OVERLAY2_ITEM2,
                "PieTheme", Arrays.asList(createLegendItem(OVERLAY2_RANGE1),
                        createLegendItem(OVERLAY2_RANGE2)));
        OverlayLegend l2 = new OverlayLegend(OVERLAY2_NAME,
                Arrays.asList(item2, item3));

        this.mockLegendData = new LegendData(Arrays.asList(l1, l2));
    }

    private SingleLegendItem createLegendItem(String title)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage bufImage = new BufferedImage(16, 16,
                BufferedImage.TYPE_3BYTE_BGR);
        try
        {
            ImageIO.write(bufImage, "jpeg", baos);
        } catch (IOException e)
        {
            e.printStackTrace();
    }
        byte[] bytesOut = baos.toByteArray();
        return new SingleLegendItem(title, bytesOut);
    }

    @Test
    public void shouldRenderNoOverlaysMessageIfEmptyLegendData()
            throws Exception
    {
        List<OverlayLegend> emptyList = Collections.emptyList();
        LegendComponent legendComponent = new LegendComponent("someTitle",
                mockMapConfig, new LegendData(emptyList));

        legendComponent.generateSAXEvents(mockContentHandler);

        watcher.verifyStartElement(BLOCK_CONTAINER_ELEMENT, null);
        watcher.verifyStartElement(BLOCK_ELEMENT, null);
        watcher.allowIntermediateCalls();
        watcher.verifyEndElement(BLOCK_ELEMENT);
        watcher.verifyStartElement(BLOCK_ELEMENT, null);
        watcher.verifyStartElement(INLINE_ELEMENT, null);
        watcher.verifyCharacters("No overlays selected");
        watcher.verifyEndElement(INLINE_ELEMENT);
        watcher.verifyEndElement(BLOCK_ELEMENT);
        watcher.verifyEndElement(BLOCK_CONTAINER_ELEMENT);
    }

    @Test
    public void shouldGenerateContainerWithBorderThatClipsOverflow()
            throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        watcher.verifyStartElement(BLOCK_CONTAINER_ELEMENT,
                createAttribute("border-style", "solid", "overflow", "hidden"));
        watcher.allowIntermediateCalls();
        watcher.verifyEndElement(BLOCK_CONTAINER_ELEMENT);
    }

    @Test
    public void shouldGenerateLegendTitle() throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        watcher.allowIntermediateCalls();
        watcher.verifyStartElement(BLOCK_ELEMENT, createAttribute(
                "background-color", "#CCC", "font-weight", "bold"));
        watcher.verifyCharacters("someTitle");
        watcher.verifyEndElement(BLOCK_ELEMENT);
    }



    @Test
    public void shouldGenerateFriendlyNameTitleForOverlay() throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        watcher.allowIntermediateCalls();
        watcher.verifyStartElement(BLOCK_ELEMENT, createAttribute(
                "background-color", "#DDD"));
        //We're expecting the maps to be rendered in reverse order, so if we've
        //added overlays in the order "overlay1,overlay2" we expect them to be
        //rendered in order "friendlyname2,friendlyname1".
        watcher.verifyCharacters(OVERLAY1_FRIENDLY_NAME);
    }

    @Test
    public void shouldShowIconNextToItemTitleIfItemHasOnlyOneIcon()
            throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        watcher.allowIntermediateCalls();
        //Find right place in verification order:
        watcher.verifyCharacters(OVERLAY1_FRIENDLY_NAME);
        watcher.verifyEndElement(BLOCK_ELEMENT);
        watcher.verifyStartElement(BLOCK_ELEMENT, null);
        watcher.verifyStartElement(BLOCK_ELEMENT, null);
        watcher.verifyStartElement(INLINE_ELEMENT, createAttribute(
                "padding-left", "1mm"));
        String expectedEnc = encodeImageInBase64(new BufferedImage(16, 16,
                BufferedImage.TYPE_3BYTE_BGR));
        watcher.verifyStartElement(EXTERNAL_GRAPHIC, createAttribute(
                "src", expectedEnc));
        watcher.verifyEndElement(EXTERNAL_GRAPHIC);
        watcher.verifyEndElement(INLINE_ELEMENT);
        watcher.verifyStartElement(INLINE_ELEMENT, createAttribute(
                "padding-left", "1mm"));
        watcher.verifyCharacters(OVERLAY1_ITEM1);
        watcher.verifyEndElement(INLINE_ELEMENT);
        watcher.verifyEndElement(BLOCK_ELEMENT);
        watcher.verifyEndElement(BLOCK_ELEMENT);
    }

    @Test
    public void shouldIndentItemTitleWithoutIconIfItemIsThematic()
            throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        watcher.allowIntermediateCalls();
        watcher.verifyStartElement(INLINE_ELEMENT, createAttribute(
                "padding-left", "2em"));
        watcher.verifyCharacters(OVERLAY2_ITEM2);
        watcher.verifyEndElement(INLINE_ELEMENT);
    }

    @Test
    public void shouldGenerateChildItemsIfItemIsThematic()
            throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        //XXX have to repeat already tested events, in order to navigate to
        //    the right place in the verification order
        watcher.allowIntermediateCalls();
        watcher.verifyCharacters(OVERLAY2_ITEM2);
        watcher.verifyEndElement(INLINE_ELEMENT);
        watcher.verifyEndElement(BLOCK_ELEMENT);
        verifyIndentedItem(OVERLAY2_RANGE1);
        verifyIndentedItem(OVERLAY2_RANGE2);
    }

    private void verifyIndentedItem(String title)
    {
        watcher.verifyStartElement(BLOCK_ELEMENT, null);
        watcher.verifyStartElement(INLINE_ELEMENT, createAttribute(
                "padding-left", "2em"));
        watcher.verifyStartElement(EXTERNAL_GRAPHIC, null);
        watcher.verifyEndElement(EXTERNAL_GRAPHIC);
        watcher.verifyEndElement(INLINE_ELEMENT);
        watcher.verifyStartElement(INLINE_ELEMENT, createAttribute(
                "padding-left", "1mm"));
        watcher.verifyCharacters(title);
        watcher.verifyEndElement(INLINE_ELEMENT);
        watcher.verifyEndElement(BLOCK_ELEMENT);
    }

}
