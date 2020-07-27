package com.pb.stratus.controller.legend;

import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.infrastructure.cache.CacheHub;
import com.pb.stratus.controller.infrastructure.cache.CacheType;
import com.pb.stratus.controller.infrastructure.cache.Cacheable;
import com.pb.stratus.controller.util.SpriteImageGenerator;
import com.pb.stratus.controller.util.SpriteImageGeneratorFactory;
import com.pb.stratus.core.configuration.Tenant;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

//FIXME since this class depends on the LegendService, why not call it
//      LegendImageService
public class SpriteImageService
{
    
    private static final Dimension LEGEND_IMAGE_ICON_SIZE 
            = new Dimension(16, 16); 
    
    private SpriteImageGeneratorFactory imageGeneratorFactory;
    
    private LegendService legendService;

    // currently injecting tenant. after multi-tenancy this need to be
    // changed. since i cannot anticipate how tenant info will come then,
    // i thought it would be better to decouple tenant info altogether.
    private Tenant tenant;
    
    private CacheHub cacheHub;

    public SpriteImageService(LegendService service,
            SpriteImageGeneratorFactory imageGeneratorFactory, Tenant tenant,
            CacheHub cacheHub)
    {
        this.legendService = service;
        this.imageGeneratorFactory = imageGeneratorFactory;
        this.tenant = tenant;
        this.cacheHub = cacheHub;
    }
    
    public BufferedImage getImage(String... overlayNames)
    {
        return getImageFromCache(overlayNames);
        }
    
    /**
     * Same logic here as well, if the overlay data is present in the cache,
     * read it from the cache otherwise request MiDev and the store it in the
     * cache. Finally all images are converted into a sprite image. Not
     * storing the sprite image in the cache as we have to do per map per
     * tenant invalidation also it would be unnecessary effort to get byte
     * array from the sprite as we do not know the type of image(jpeg,
     * png...) as the MiDev does not prescribe to any particular format.
     * @param overlayNames
     * @return
     */
    private BufferedImage getImageFromCache(String... overlayNames)
    {
        List<BufferedImage> images = new ArrayList<BufferedImage>();
        if (legendService instanceof CachingLegendService) {
            LegendCacheData legendCacheData= LegendCacheHelper.createLegendDataFromCache
                    (getTenantCache(), tenant, overlayNames);
            images.addAll(legendCacheData.getLegendData().getIcons());

            if(!legendCacheData.getLegendsNotPresentInCache().isEmpty())
            {
                String [] legendsNotPresentInCache = legendCacheData
                        .getOverlaysNotPresentInCacheAsArray();
                LegendData legendData = getLegendData(legendsNotPresentInCache);
                LegendCacheHelper.putLegendDataInCache(getTenantCache(), tenant,
                        legendData);
                images.addAll(legendData.getIcons());
            }
        } else {
            LegendData legendData = getLegendData(overlayNames);
            images.addAll(legendData.getIcons());
        }

        return createSpriteImage(images);
    }

    private LegendData getLegendData(String[] overlayNames)
    {
        return legendService.getLegendData(LocaleResolver.getLocale(), null,null,
                overlayNames);
    }

    private BufferedImage createSpriteImage(List<BufferedImage> images)
    {
        SpriteImageGenerator generator = imageGeneratorFactory
                .createSpriteImageGenerator(LEGEND_IMAGE_ICON_SIZE);
        for(BufferedImage image : images)
        {
            generator.addImage(image);
        }
        return generator.createSpriteImage();
    }

    /**
     * making it protected for testing.
     * @return
     */
    protected Cacheable getTenantCache()
    {
         return this.cacheHub.getCacheForTenant(tenant, CacheType.LEGEND_CACHE);
}
}
