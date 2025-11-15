package org.geysermc.extension.capefix;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;

public class MojangCapeFix implements Extension {
    
    @Subscribe
    public void onPreInitialize(GeyserPreInitializeEvent event) {
        this.logger().info("=========================================");
        this.logger().info("  MojangCapeFix Extension Starting...");
        this.logger().info("  Fixing blue cape texture bug!");
        this.logger().info("=========================================");
        
        this.eventBus().subscribe(this, new CapeFixListener(this));
        
        this.logger().info("MojangCapeFix loaded successfully!");
    }
}
