package com.pb.stratus.controller.print.render;

import com.pb.stratus.controller.print.LayerRenderParams;

import java.awt.image.BufferedImage;

public interface LayerRenderer
{
    public BufferedImage render(LayerRenderParams layerRenderParams);
}
