package net.spartanb312.base.event.decentraliized;

import net.spartanb312.base.core.event.decentralization.DecentralizedEvent;
import net.spartanb312.base.event.events.render.RenderWorldEvent;

public class DecentralizedRenderWorldEvent extends DecentralizedEvent<RenderWorldEvent> {
    public static DecentralizedRenderWorldEvent instance = new DecentralizedRenderWorldEvent();
}
