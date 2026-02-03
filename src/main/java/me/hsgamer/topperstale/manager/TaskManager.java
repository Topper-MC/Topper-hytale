package me.hsgamer.topperstale.manager;

import com.hypixel.hytale.server.core.util.thread.TickingThread;
import me.hsgamer.topper.agent.core.Agent;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskManager {
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);

    public Agent createTaskAgent(Runnable runnable, long delayMillis) {
        long finalDelayMillis = delayMillis * TickingThread.TPS;
        return new Agent() {
            private ScheduledFuture<?> scheduledFuture;

            @Override
            public void start() {
                scheduledFuture = scheduler.scheduleAtFixedRate(runnable, finalDelayMillis, finalDelayMillis, TimeUnit.MILLISECONDS);
            }

            @Override
            public void stop() {
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }
            }
        };
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
