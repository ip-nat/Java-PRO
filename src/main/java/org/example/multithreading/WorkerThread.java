package org.example.multithreading;

//отдельный рабочий поток
public class WorkerThread extends Thread {

    private final TaskQueue taskQueue;
    private volatile boolean isInterrupted = false;

    public WorkerThread(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void interruptThread() {
        isInterrupted = true;
        interrupt();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Runnable task = taskQueue.getTask();
                task.run();
            } catch (InterruptedException e) {
                if (isInterrupted) {
                    return;
                }
                Thread.currentThread().interrupt();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
}
