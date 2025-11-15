package org.geysermc.extension.capefix;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.event.bedrock.SessionSkinApplyEvent;
import org.geysermc.geyser.api.skin.Cape;

public class MojangCapeFix implements Extension {
    
    @Subscribe
    public void onSkinApply(SessionSkinApplyEvent event) {
        if (event.bedrock()) {
            return; // Only process Java players
        }
        
        Cape currentCape = event.skinData().cape();
        
        if (currentCape == null || currentCape.failed()) {
            return;
        }
        
        try {
            this.logger().info("Fixing cape for: " + event.username());
            
            Cape fixedCape = CapeFixListener.fixCape(currentCape, this);
            
            if (fixedCape != null && fixedCape != currentCape) {
                event.cape(fixedCape);
                this.logger().info("✓ Cape fixed for: " + event.username());
            }
            
        } catch (Exception e) {
            this.logger().error("Failed to fix cape for " + event.username(), e);
        }
    }
}
