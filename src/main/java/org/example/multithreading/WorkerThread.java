package org.example.multithreading;

public class WorkerThread extends Thread {

    private final ThreadPool pool;

    public WorkerThread(ThreadPool pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        while (true) {
            Runnable task = pool.getTask();
            if (task == null) {
                break;
            }
            task.run();
        }
    }
}
