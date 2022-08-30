package me.thediamondsword5.moloch.event.decentralized;

import net.spartanb312.base.core.event.decentralization.DecentralizedEvent;
import me.thediamondsword5.moloch.event.events.render.RenderWorldPostEventCenter;

public class DecentralizedRenderWorldPostEvent extends DecentralizedEvent<RenderWorldPostEventCenter> {
    public static DecentralizedRenderWorldPostEvent instance = new DecentralizedRenderWorldPostEvent();
}
