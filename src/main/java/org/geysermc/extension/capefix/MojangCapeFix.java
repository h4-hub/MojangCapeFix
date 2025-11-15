package org.geysermc.extension.capefix;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.event.bedrock.SessionSkinApplyEvent;
import org.geysermc.geyser.api.skin.Cape;

public class MojangCapeFix implements Extension {
    
    @Subscribe
    public void onSkinApply(SessionSkinApplyEvent event) {
        this.logger().info("=== SessionSkinApplyEvent triggered ===");
        this.logger().info("Player: " + event.username());
        this.logger().info("Is Bedrock: " + event.bedrock());
        
        if (event.bedrock()) {
            this.logger().info("Skipping Bedrock player");
            return;
        }
        
        Cape currentCape = event.skinData().cape();
        
        this.logger().info("Current cape: " + (currentCape != null ? "exists" : "null"));
        
        if (currentCape != null) {
            this.logger().info("Cape failed: " + currentCape.failed());
            this.logger().info("Cape textureUrl: " + currentCape.textureUrl());
            this.logger().info("Cape capeId: " + currentCape.capeId());
        }
        
        if (currentCape == null || currentCape.failed()) {
            this.logger().info("No valid cape to fix");
            return;
        }
        
        try {
            this.logger().info("Attempting cape fix for: " + event.username());
            
            Cape fixedCape = CapeFixListener.fixCape(currentCape, this);
            
            if (fixedCape != null && fixedCape != currentCape) {
                event.cape(fixedCape);
                this.logger().info("✓ Cape fixed and applied for: " + event.username());
            } else {
                this.logger().info("Cape fix returned same cape object");
            }
            
        } catch (Exception e) {
            this.logger().error("Failed to fix cape for " + event.username(), e);
        }
    }
}
