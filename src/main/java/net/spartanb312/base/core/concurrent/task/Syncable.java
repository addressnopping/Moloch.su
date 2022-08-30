package net.spartanb312.base.core.concurrent.task;

import net.spartanb312.base.core.concurrent.utils.Syncer;

abstract class Syncable implements Runnable {
    protected Syncer syncer;
}
