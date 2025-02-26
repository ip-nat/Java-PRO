package org.example.multithreading;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool {

    private final List<WorkerThread> workers;
    private final List<Runnable> tasks;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Lock lock = new ReentrantLock();
    private final Condition taskAvailable = lock.newCondition();

    public ThreadPool(int capacity) {
        this.workers = new LinkedList<>();
        this.tasks = new LinkedList<>();

        for (int i = 0; i < capacity; i++) {
            WorkerThread worker = new WorkerThread(this);
            workers.add(worker);
            worker.start();
        }
        System.out.println("Все потоки инициализированы. Количество потоков: " + workers.size());
    }

    protected Runnable getTask() {
        lock.lock();
        int currentThread = System.identityHashCode(Thread.currentThread());
        try {
            if (!isRunning.get() && tasks.isEmpty()) {
                System.out.println("Пул остановлен и задач нет. Поток " + currentThread + " завершает работу");
                return null;
            }
            while (tasks.isEmpty() && isRunning.get()) {
                try {
                    System.out.println("Поток " + currentThread + " ожидает новую задачу");
                    taskAvailable.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Поток " + currentThread + " был прерван");
                }
            }
            Runnable task = tasks.removeFirst();
            System.out.println("Поток " + currentThread + " взял в работу задачу " + System.identityHashCode(task));
            return task;
        } finally {
            lock.unlock();
        }
    }

    protected void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("Задача не может быть пустой");
        }
        if (!isRunning.get()) {
            throw new IllegalStateException("Пул потоков остановлен. Задача не может быть выполнена");
        }
        lock.lock();
        tasks.addLast(task);
        System.out.println("В пул добавлена задача " + System.identityHashCode(task));
        taskAvailable.signalAll();
        lock.unlock();
    }

    protected synchronized void shutdown() {
        isRunning.set(false);
        System.out.println("Пул потоков остановлен");
        lock.lock();
        taskAvailable.signalAll();
        lock.unlock();
    }

    protected void awaitTermination() throws InterruptedException {
        System.out.println("Завершение всех задач в пуле после его остановки");
        Thread terminationThread = new Thread(() -> {
            while (workers.stream()
                    .anyMatch(WorkerThread::isAlive)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        terminationThread.setDaemon(true);
        terminationThread.start();
        terminationThread.join();
        System.out.println("Все задачи выполнены");
    }

}
