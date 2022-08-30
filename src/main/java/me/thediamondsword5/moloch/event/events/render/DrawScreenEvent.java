package me.thediamondsword5.moloch.event.events.render;

import net.spartanb312.base.event.EventCenter;

public class DrawScreenEvent extends EventCenter {
    public static class Layer1 extends DrawScreenEvent {
        public Layer1() {}
    }

    public static class Layer2 extends DrawScreenEvent {
        public Layer2() {}
    }

    public static class Chat extends DrawScreenEvent {
        public Chat() {}
    }
}
