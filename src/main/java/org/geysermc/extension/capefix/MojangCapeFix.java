package org.geysermc.extension.capefix;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;

public class MojangCapeFix implements Extension {
    
    private CapeFixListener capeFixListener;
    
    @Subscribe
    public void onPreInitialize(GeyserPreInitializeEvent event) {
        this.logger().info("=========================================");
        this.logger().info("  MojangCapeFix Extension Starting...");
        this.logger().info("  Fixing blue cape texture bug!");
        this.logger().info("=========================================");
        
        capeFixListener = new CapeFixListener(this);
        this.eventBus().subscribe(this, capeFixListener);
        
        this.logger().info("MojangCapeFix loaded successfully!");
    }
}
