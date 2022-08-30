package net.spartanb312.base.core.config;

import net.spartanb312.base.core.event.decentralization.DecentralizedEvent;
import net.spartanb312.base.core.event.decentralization.EventData;
import net.spartanb312.base.core.event.decentralization.Listenable;
import net.spartanb312.base.core.event.decentralization.ListenableImpl;
import net.spartanb312.base.core.concurrent.task.Task;

import java.util.concurrent.ConcurrentHashMap;

public class ListenableContainer extends ConfigContainer implements Listenable {

    private final ConcurrentHashMap<DecentralizedEvent<? extends EventData>, Task<? extends EventData>> listenerMap = new ConcurrentHashMap<>();

    @Override
    public ConcurrentHashMap<DecentralizedEvent<? extends EventData>, Task<? extends EventData>> listenerMap() {
        return listenerMap;
    }

    @Override
    public void subscribe() {
        listenerMap.forEach(DecentralizedEvent::register);
    }

    @Override
    public void unsubscribe() {
        listenerMap.forEach(DecentralizedEvent::unregister);
    }

    public <T extends EventData> void listener(Class<? extends DecentralizedEvent<T>> eventClass, Task<T> action) {
        ListenableImpl.listener(this, eventClass, action);
    }

}
