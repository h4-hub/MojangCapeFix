package org.geysermc.extension.capefix;

import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.skin.Cape;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CapeFixListener {
    
    public static Cape fixCape(Cape currentCape, Extension extension) {
        if (currentCape == null || currentCape.failed()) {
            return currentCape;
        }
        
        try {
            byte[] capeData = currentCape.data();
            
            if (capeData == null || capeData.length == 0) {
                return currentCape;
            }
            
            BufferedImage originalCape = bytesToImage(capeData);
            
            if (originalCape == null) {
                return currentCape;
            }
            
            BufferedImage fixedCape = fixCapeTexture(originalCape);
            byte[] fixedCapeData = imageToBytes(fixedCape);
            
            return new Cape(
                currentCape.textureUrl(),
                currentCape.capeId(),
                fixedCapeData,
                false
            );
            
        } catch (Exception e) {
            extension.logger().error("Failed to fix cape", e);
            return currentCape;
        }
    }
    
    private static BufferedImage bytesToImage(byte[] imageData) {
        try {
            return ImageIO.read(new java.io.ByteArrayInputStream(imageData));
        } catch (IOException e) {
            return null;
        }
    }
    
    private static BufferedImage fixCapeTexture(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        BufferedImage fixed = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = original.getRGB(x, y);
                
                int alpha = (pixel >> 24) & 0xFF;
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                
                if (alpha == 0) {
                    fixed.setRGB(x, y, 0x00000000);
                } else {
                    int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    fixed.setRGB(x, y, newPixel);
                }
            }
        }
        
        return fixed;
    }
    
    private static byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}
