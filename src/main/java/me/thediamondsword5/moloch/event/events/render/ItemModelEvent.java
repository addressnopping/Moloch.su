package me.thediamondsword5.moloch.event.events.render;

import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.spartanb312.base.event.EventCenter;

public class ItemModelEvent extends EventCenter {
    public float swingProgress;
    public EnumHand hand;

    public ItemModelEvent(EnumHand hand, float swingProgress) {
        this.hand = hand;
        this.swingProgress = swingProgress;
    }

    public ItemModelEvent() {
    }

    public ItemModelEvent(EnumHand hand) {
        this.hand = hand;
    }

    public static class Normal extends ItemModelEvent {
        public Normal(EnumHand hand, float swingProgress) {
            super(hand, swingProgress);
        }
    }

    public static class Pre extends ItemModelEvent {
        public Pre(EnumHand hand) {
            super(hand);
        }
    }

    public static class Hit extends ItemModelEvent {
        public Hit() {
            super();
        }
    }

    public static class OtherOne extends ItemModelEvent {
        public OtherOne(EnumHand hand) {
            super(hand);
        }
    }

    public static class OtherTwo extends ItemModelEvent {
        public OtherTwo(EnumHand hand) {
            super(hand);
        }
    }
}
