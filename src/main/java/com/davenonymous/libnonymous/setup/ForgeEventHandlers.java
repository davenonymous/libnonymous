package com.davenonymous.libnonymous.setup;

import com.davenonymous.libnonymous.render.RenderTickCounter;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if(event.phase == TickEvent.RenderTickEvent.Phase.START) {
            RenderTickCounter.renderTicks++;
        }
    }
}
