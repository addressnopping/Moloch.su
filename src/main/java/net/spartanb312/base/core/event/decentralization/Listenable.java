package net.spartanb312.base.core.event.decentralization;

import net.spartanb312.base.core.concurrent.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public interface Listenable {

    ConcurrentHashMap<DecentralizedEvent<? extends EventData>, Task<? extends EventData>> listenerMap();

    void subscribe();

    void unsubscribe();

}
