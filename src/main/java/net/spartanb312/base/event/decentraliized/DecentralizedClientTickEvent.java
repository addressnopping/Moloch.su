package net.spartanb312.base.event.decentraliized;

import net.spartanb312.base.core.event.decentralization.DecentralizedEvent;
import net.spartanb312.base.core.event.decentralization.EventData;

public class DecentralizedClientTickEvent extends DecentralizedEvent<EventData> {
    public static DecentralizedClientTickEvent instance = new DecentralizedClientTickEvent();
}
