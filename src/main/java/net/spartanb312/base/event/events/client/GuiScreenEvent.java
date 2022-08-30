package net.spartanb312.base.event.events.client;

import net.spartanb312.base.event.EventCenter;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenEvent extends EventCenter {

    private final GuiScreen screen;

    public GuiScreenEvent(GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return screen;
    }

    public static class Displayed extends GuiScreenEvent {
        public Displayed(GuiScreen screen) {
            super(screen);
        }
    }

    public static class Closed extends GuiScreenEvent {
        public Closed(GuiScreen screen) {
            super(screen);
        }
    }

}
