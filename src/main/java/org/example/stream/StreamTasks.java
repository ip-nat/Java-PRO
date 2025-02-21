package org.example.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.stream.StreamTasks.Employee.Position.ANALYST;
import static org.example.stream.StreamTasks.Employee.Position.ENGINEER;
import static org.example.stream.StreamTasks.Employee.Position.MANAGER;

public class StreamTasks {

    public static void main(String[] args) {
        System.out.println("1. Удаление дубликатов из списка");
        List<Integer> duplicatesList = Arrays.asList(1, 2, 2, 3, 4, 4, 5);
        System.out.println("Список с дубликатами: " + duplicatesList);
        List<Integer> distinctList = duplicatesList.stream()
                .distinct()
                .toList();
        System.out.println("Преобразованный список с уникальными значениями: " + distinctList);
        System.out.println("_____________________________________________________");


        System.out.println("2. Поиск 3-го наибольшего числа");
        List<Integer> numbers_1 = Arrays.asList(5, 2, 10, 9, 4, 3, 10, 1, 13);
        System.out.println("Список чисел: " + numbers_1);
        Optional<Integer> thirdLargest = numbers_1.stream()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst();
        Integer largestResult = thirdLargest.orElseThrow(()
                -> new NoSuchElementException("Третье наибольшее число не найдено"));
        System.out.println("Третье наибольшее число: " + largestResult);
        System.out.println("_____________________________________________________");

        System.out.println("3. Поиск 3-го наибольшего «уникального» числа");
        List<Integer> numbers_2= Arrays.asList(5, 2, 10, 9, 4, 3, 10, 1, 13);
        System.out.println("Список чисел: " + numbers_2);
        Optional<Integer> thirdLargestUnique = numbers_2.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst();
        Integer largestUniqueResult = thirdLargestUnique.orElseThrow(()
                -> new NoSuchElementException("Третье наибольшее уникальное число не найдено"));
        System.out.println("Третье наибольшее уникальное число: " + largestUniqueResult);
        System.out.println("_____________________________________________________");

        System.out.println("4. Список имен 3 самых старших сотрудников с должностью «Инженер», в порядке убывания");
        List<Employee> employees = Arrays.asList(
                new Employee("Ульяна", 35, ANALYST),
                new Employee("Виктор", 29, ENGINEER),
                new Employee("Ольга", 32, ANALYST),
                new Employee("Денис", 34, MANAGER),
                new Employee("Наталья", 33, ENGINEER),
                new Employee("Андрей", 34, ENGINEER)

        );
        System.out.println("Список сотрудников: " + employees);
        List<String> topOldEngineers = employees.stream()
                .filter(e -> ENGINEER.equals(e.position()))
                .sorted(Comparator.comparingInt(Employee::age)
                        .reversed())
                .limit(3)
                .map(Employee::name)
                .toList();
        System.out.println("Топ 3 старших инженера: " + topOldEngineers);
        System.out.println("_____________________________________________________");

        System.out.println("5. Средний возраст сотрудников с должностью «Инженер»");
        double averageAge = employees.stream()
                .filter(e -> ENGINEER.equals(e.position()))
                .collect(Collectors.averagingDouble(Employee::age));
        System.out.println("Средний возраст инженеров: " + averageAge);
        System.out.println("_____________________________________________________");

        System.out.println("6. Самое длинное слово в списке");
        List<String> waveFunction = Arrays.asList(
                "волновая", "функция", "первоначально", "находящаяся",
                "в", "суперпозиции", "нескольких", "собственных",
                "состояний", "сводится", "к", "одному",
                "собственному", "состоянию", "из-за",
                "взаимодействия", "с", "внешним", "миром"
        );
        System.out.println("Список слов: " + waveFunction);
        Optional<String> longestWordFromListOptional = waveFunction.stream()
                .max(Comparator.comparingInt(String::length));
        String longestWord_1 = longestWordFromListOptional.orElseThrow(()
                -> new NoSuchElementException("Самое длинное слово в списке не найдено"));
        System.out.println("Самое длинное слово: " + longestWord_1);
        System.out.println("_____________________________________________________");

        System.out.println("7. HashMap: слово - количество вхождений");
        String text = "алло прием прием как слышно прием алло";
        System.out.println("Строка с набором слов: " + text);
        Map<String, Long> wordCount = Arrays.stream(text.split(" "))
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
        System.out.println("Количество вхождений: " + wordCount);
        System.out.println("_____________________________________________________");

        System.out.println("8. Сортировка строк по длине и алфавиту");
        System.out.println("Список слов: " + waveFunction);
        List<String> sortedStrings = waveFunction.stream()
                .sorted(Comparator.comparingInt(String::length)
                        .thenComparing(String::compareTo))
                .toList();
        System.out.println("Сортированные строки в порядке увеличения длины: " + sortedStrings);
        System.out.println("_____________________________________________________");

        System.out.println("9. Самое длинное слово из массива строк с наборами слов");
        String[] kotelnikov = {
                "Теорема Котельникова гласит что сигнал",
                "можно восстановить из его значений",
                "в дискретные моменты при условии",
                "что частота дискретизации в два",
                "раза превышает максимальную частоту сигнала"
        };
        System.out.println("Массив строк: " + Arrays.toString(kotelnikov));
        Optional<String> longestWordFromArrayOptional = Arrays.stream(kotelnikov)
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .max(Comparator.comparingInt(String::length));
        String longestWord_2 = longestWordFromArrayOptional.orElseThrow(()
                -> new NoSuchElementException("Самое длинное слово в массиве не найдено"));
        System.out.println("Самое длинное слово в массиве: " + longestWord_2);

    }

    record Employee(String name, int age, StreamTasks.Employee.Position position) {

        enum Position {
            ENGINEER, ANALYST, MANAGER
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", position='" + position + '\'' +
                    '}';
        }
    }

}
