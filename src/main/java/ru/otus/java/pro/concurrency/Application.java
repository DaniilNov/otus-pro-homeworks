package ru.otus.java.pro.concurrency;

public class Application {
    public static void main(String[] args) {
        CustomThreadPool threadPool = new CustomThreadPool(5);

        for (int i = 0; i < 10; i++) {
            int taskNumber = i;
            threadPool.execute(() -> {
                System.out.println("Executing task " + taskNumber + " by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        threadPool.shutdown();
    }
}
