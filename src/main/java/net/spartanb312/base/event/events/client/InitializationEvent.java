package net.spartanb312.base.event.events.client;

import net.spartanb312.base.event.EventCenter;

/**
 * We use this to launch our client
 */
public class InitializationEvent extends EventCenter {
    public static class PreInitialize extends InitializationEvent {
    }

    public static class Initialize extends InitializationEvent {
    }

    public static class PostInitialize extends InitializationEvent {
    }
}
