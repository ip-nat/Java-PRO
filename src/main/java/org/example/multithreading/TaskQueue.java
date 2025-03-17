package org.example.multithreading;

import java.util.LinkedList;

//очередь задач
public class TaskQueue {

    private final LinkedList<Runnable> queue = new LinkedList<>();

    public synchronized void addTask(Runnable task) {
        queue.addLast(task);
        notifyAll(); // Оживление ждущих потоков
    }

    public synchronized Runnable getTask() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.removeFirst();
    }

    public synchronized int size() {
        return queue.size();
    }

}
