package me.thediamondsword5.moloch.event.events.render;

import net.spartanb312.base.core.event.decentralization.EventData;
import net.spartanb312.base.event.EventCenter;

public final class RenderWorldPostEventCenter extends EventCenter implements EventData {

    private final float partialTicks;
    private final Pass pass;

    public RenderWorldPostEventCenter(float partialTicks, int pass) {
        this.partialTicks = partialTicks;
        this.pass = Pass.values()[pass];
    }

    public final Pass getPass() {
        return this.pass;
    }

    public final float getPartialTicks() {
        return partialTicks;
    }

    public enum Pass {
        ANAGLYPH_CYAN, ANAGLYPH_RED, NORMAL
    }

}
