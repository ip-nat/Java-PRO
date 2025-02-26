package org.example.multithreading;

import java.util.Random;

public class Main {

    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        ThreadPool pool = new ThreadPool(3);

        for (int i = 1; i < 10; i++) {
            System.out.println("MAIN: Добавление в очередь новой задачи");
            int sleepTime = random.nextInt(1000 - 500 + 1) + 500;
            pool.execute(() -> {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        System.out.println("MAIN: Остановка пула и ожидание завершения всех задач");
        pool.shutdown();
        pool.awaitTermination();
        try {
            System.out.println("MAIN: Попытка добавить новую задачу после остановки пула");
            pool.execute(() -> System.out.println("Новая задача добавлена после остановки пула"));
        } catch (IllegalStateException e) {
            System.out.println("MAIN: Ошибка: " + e.getMessage());
        }
    }

}
