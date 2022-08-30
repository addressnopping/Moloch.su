package me.thediamondsword5.moloch.event.events.render;

import net.spartanb312.base.event.EventCenter;

public class FOVItemModifyEvent extends EventCenter {
    public float fov;

    public FOVItemModifyEvent(float fov) {
        this.fov = fov;
    }
}
