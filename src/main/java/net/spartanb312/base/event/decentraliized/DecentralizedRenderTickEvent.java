package net.spartanb312.base.event.decentraliized;

import net.spartanb312.base.core.event.decentralization.DecentralizedEvent;
import net.spartanb312.base.event.events.render.RenderOverlayEvent;

public class DecentralizedRenderTickEvent extends DecentralizedEvent<RenderOverlayEvent> {
    public static DecentralizedRenderTickEvent instance = new DecentralizedRenderTickEvent();
}
