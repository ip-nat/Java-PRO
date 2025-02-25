package org.example.multithreading;

import java.util.LinkedList;
import java.util.List;

public class ThreadPool {

    private final List<WorkerThread> workers;
    private final List<Runnable> tasks;
    private volatile boolean isRunning = true;

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

    protected synchronized Runnable getTask() {
        int currentThread = System.identityHashCode(Thread.currentThread());
        if (!isRunning && tasks.isEmpty()) {
            System.out.println("Пул остановлен и задач нет. Поток " + currentThread + " завершает работу");
            return null;
        }
        while (tasks.isEmpty() && isRunning) {
            try {
                System.out.println("Поток " + currentThread + " ожидает новую задачу");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Поток был прерван");
            }
        }
        Runnable task = tasks.removeFirst();
        System.out.println("Поток " + currentThread + " взял в работу задачу " + System.identityHashCode(task));
        return task;
    }

    public synchronized void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("Задача не может быть пустой");
        }
        if (!isRunning) {
            throw new IllegalStateException("Пул потоков остановлен. Задача не может быть выполнена");
        }
        tasks.addLast(task);
        System.out.println("В пул добавлена задача " + System.identityHashCode(task));
        notifyAll();
    }

    public synchronized void shutdown() {
        isRunning = false;
        System.out.println("Пул потоков остановлен");
        notifyAll();
    }

    public synchronized void awaitTermination() throws InterruptedException {
        System.out.println("Завершение всех задач в пуле после его остановки");
        while (workers.stream()
                .anyMatch(WorkerThread::isAlive)) {
            wait(100);
        }
    }

}
