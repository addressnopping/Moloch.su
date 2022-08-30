package net.spartanb312.base.event.events.render;

import net.spartanb312.base.event.EventCenter;

public class RenderModelEvent extends EventCenter {
    public boolean rotating = false;
    public float pitch = 0;

    public RenderModelEvent(){
        super();
    }
}
