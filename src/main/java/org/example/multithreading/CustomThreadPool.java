package org.example.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//управление пулом потоков
public class CustomThreadPool {

    private final TaskQueue taskQueue = new TaskQueue();
    private final List<WorkerThread> workerThreads = new ArrayList<>();
    private volatile boolean isShutdown = false;

    Consumer<Integer> sleepFunc = (countSleep) -> {
        try {
            Thread.sleep(countSleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    };

    public CustomThreadPool(int capacity) {
        for (int i = 0; i < capacity; i++) {
            WorkerThread worker = new WorkerThread(taskQueue);
            workerThreads.add(worker);
            worker.start();
        }
        System.out.printf("Создан кастомный ThreadPool из: %s потоков%n", capacity);
    }

    public void execute(Runnable task) {
        if (isShutdown) {
            throw new IllegalStateException("Пул потоков остановлен, новые задачи не принимаются");
        }
        taskQueue.addTask(task);
    }

    public void shutdown() {
        isShutdown = true;
        for (WorkerThread worker : workerThreads) {
            worker.interruptThread();
        }
    }

    // Ожидание завершения пула потоков
    public void awaitTermination() throws InterruptedException {
        while (true) {
            boolean isComplete = workerThreads.stream()
                    .map(th -> th.getState() == Thread.State.WAITING)
                    .reduce(Boolean::logicalAnd)
                    .orElse(false);
            sleepFunc.accept(100);
            // Коньюкция статусов всех потоков пула.
            // Если все потоки в состоянии WAITING и очередь пустая -> продолжаем работу main
            if (isComplete && taskQueue.size() == 0) return;
        }
    }

}
