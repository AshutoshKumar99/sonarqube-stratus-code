package com.pb.stratus.controller.print.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageReader
{
    /**
     * 
     * @param url
     * @return
     * @throws IOException
     */
	public BufferedImage readFromUrl(URL url) throws IOException {
        InputStream is = url.openConnection().getInputStream();
        return ImageIO.read(is);
    }

	/**
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public BufferedImage readFromStream(InputStream is) throws IOException {
		return ImageIO.read(is);
	}

}
