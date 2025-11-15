package org.geysermc.extension.capefix;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.event.bedrock.SessionSkinApplyEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.skin.SkinProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CapeFixListener {
    
    private final Extension extension;
    
    public CapeFixListener(Extension extension) {
        this.extension = extension;
    }
    
    @Subscribe
    public void onSkinApply(SessionSkinApplyEvent event) {
        GeyserConnection connection = event.connection();
        
        SkinProvider.Cape cape = event.cape();
        
        if (cape == null) {
            return;
        }
        
        try {
            extension.logger().info("Fixing cape for: " + connection.javaUsername());
            
            byte[] capeData = cape.capeData();
            BufferedImage originalCape = bytesToImage(capeData);
            
            if (originalCape == null) {
                return;
            }
            
            BufferedImage fixedCape = fixCapeTexture(originalCape);
            byte[] fixedCapeData = imageToBytes(fixedCape);
            
            SkinProvider.Cape fixedCapeObject = new SkinProvider.Cape(
                cape.textureUrl(),
                cape.capeId(),
                fixedCapeData,
                System.currentTimeMillis(),
                false
            );
            
            event.cape(fixedCapeObject);
            
            extension.logger().info("✓ Cape fixed for: " + connection.javaUsername());
            
        } catch (Exception e) {
            extension.logger().error("Failed to fix cape", e);
        }
    }
    
    private BufferedImage bytesToImage(byte[] imageData) {
        try {
            return ImageIO.read(new java.io.ByteArrayInputStream(imageData));
        } catch (IOException e) {
            return null;
        }
    }
    
    private BufferedImage fixCapeTexture(BufferedImage original) {
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
    
    private byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}
