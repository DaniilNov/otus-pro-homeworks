package ru.otus.java.pro.concurrency;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CustomThreadPool {
    private final List<Worker> workers;
    private final Queue<Runnable> taskQueue;
    private volatile boolean isShutdown;

    /**
     * Создает CustomThreadPool с указанным количеством рабочих потоков
     *
     * @param capacity количество рабочих потоков в пуле
     */
    public CustomThreadPool(int capacity) {
        this.taskQueue = new LinkedList<>();
        this.workers = new LinkedList<>();
        this.isShutdown = false;

        for (int i = 0; i < capacity; i++) {
            Worker worker = new Worker(taskQueue, this);
            workers.add(worker);
            worker.start();
        }
    }

    /**
     * Добавляет задачу для выполнения в пул потоков
     *
     * @param task задача для выполнения
     * @throws IllegalStateException если пул потоков завершен
     */
    public void execute(Runnable task) {
        if (isShutdown) {
            throw new IllegalStateException("Пул потоков завершен, невозможно принять новые задачи");
        }
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notify();
        }
    }

    /**
     * Инициирует упорядоченное завершение работы пула потоков
     * Новые задачи не будут приниматься, и все рабочие потоки завершат работу
     * после выполнения всех задач в очереди
     */
    public void shutdown() {
        isShutdown = true;
        synchronized (taskQueue) {
            taskQueue.notifyAll();
        }
    }

    /**
     * Проверяет, завершен ли пул потоков
     *
     * @return true, если пул потоков завершен, иначе false
     */
    public boolean isShutdown() {
        return isShutdown;
    }
}