package ru.otus.java.pro.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * Worker выполняет задачи из очереди до завершения пула
 */
public class Worker extends Thread {
    private static final Logger log = LoggerFactory.getLogger(Worker.class);

    private final Queue<Runnable> taskQueue;
    private final CustomThreadPool threadPool;

    public Worker(Queue<Runnable> taskQueue, CustomThreadPool threadPool) {
        this.taskQueue = taskQueue;
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        while (true) {
            Runnable task;
            synchronized (taskQueue) {
                while (taskQueue.isEmpty() && !threadPool.isShutdown()) {
                    try {
                        taskQueue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (threadPool.isShutdown() && taskQueue.isEmpty()) {
                    return;
                }
                task = taskQueue.poll();
            }
            if (task != null) {
                try {
                    task.run();
                } catch (RuntimeException e) {
                    log.error("Ошибка при выполнении задачи", e);
                }
            }
        }
    }
}