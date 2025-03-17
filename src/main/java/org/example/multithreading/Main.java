package org.example.multithreading;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        CustomThreadPool pool = new CustomThreadPool(5);

        for (int i = 0; i < 10; i++) {
            int index = i;
            System.out.println("MAIN: Добавление в очередь новой задачи " + index);
            pool.execute(() -> System.out.println("Задача " + index + " выполнена"));
        }

        System.out.println("MAIN: Остановка пула и ожидание завершения всех задач");
        pool.shutdown();
        pool.awaitTermination();
        System.out.println("MAIN: Пул потоков завершен");
        try {
            System.out.println("MAIN: Попытка добавить новую задачу после остановки пула");
            pool.execute(() -> System.out.println("Новая задача добавлена после остановки пула"));
        } catch (IllegalStateException e) {
            System.out.println("MAIN: Ошибка: " + e.getMessage());
        }
    }

}
