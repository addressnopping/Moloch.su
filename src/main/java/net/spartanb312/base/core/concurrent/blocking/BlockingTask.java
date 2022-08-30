package net.spartanb312.base.core.concurrent.blocking;

import net.spartanb312.base.core.concurrent.task.Task;

public interface BlockingTask extends Task<BlockingContent> {
    @Override
    void invoke(BlockingContent unit);
}
