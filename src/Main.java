import model.*;
import manager.*;
import exception.*;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        String fileName = "data.csv";
        File file = new File(fileName);
        if (!file.isFile()) {
            try {
                Files.createFile(Paths.get(fileName));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла.");
            }
        }

        // Спринт_8 начало
        TaskManager manager = new FileBackedTaskManager(file);
        if (manager.getAllTasks().isEmpty()) {
            System.out.println("Создание объектов ...");

            Task task1 = new Task("Задача 1", "Первый обычный таск", Status.NEW,
                    LocalDateTime.of(2025, 9, 26, 17, 0), Duration.ofMinutes(60));
            manager.add(task1);

            Task task2 = new Task("Задача 2", "Второй обычный таск", Status.NEW,
                    LocalDateTime.of(2025, 9, 26, 19, 0), Duration.ofMinutes(60));
            manager.add(task2);

            Long epicNum1 = manager.add(new Epic("Эпик 1", "Первый эпик"));
            Long epicNum2 = manager.add(new Epic("Эпик 2", "Второй эпик"));

            SubTask subTask1 = new SubTask("Первый сабтаск первого эпика", "", Status.NEW, epicNum1,
                    LocalDateTime.of(2025, 9, 25, 12, 0), Duration.ofMinutes(20));
            SubTask subTask2 = new SubTask("Второй сабтаск первого эпика", "", Status.NEW, epicNum1,
                    LocalDateTime.of(2025, 9, 25, 13, 0), Duration.ofMinutes(20));
            SubTask subTask3 = new SubTask("Первый сабтаск второго эпика", "", Status.NEW, epicNum2,
                    LocalDateTime.of(2025, 9, 25, 12, 30), Duration.ofMinutes(20));
            manager.add(subTask1);
            manager.add(subTask2);
            manager.add(subTask3);
        }

        Task task2 = new Task("Задача 3", "", Status.NEW,
                LocalDateTime.of(2025, 9, 25, 9, 30), Duration.ofMinutes(30));
        manager.add(task2);

        printAll(manager);
        System.out.println("\nЗадачи по приоритету:");
        manager.printPrioritization();

        System.out.println("\nЗагрузка из файла:");
        FileBackedTaskManager loadFromFile = FileBackedTaskManager.loadFromFile(file);
        printAll(loadFromFile);
        // Спринт_8 конец
    }

    // Сценарий для проверки просмотра истории:
    public static void printAll(TaskManager manager) {
        System.out.println("Задачи:");
        manager.getAllTasks().forEach(task -> {
            manager.getTask(task.getId());
            System.out.println(task);
        });
        System.out.println("Эпики:");
        manager.getAllEpics().forEach(epic -> {
            manager.getEpic(epic.getId());
            System.out.println(epic);
            manager.getAllSubTasks().forEach(task -> {
                manager.getSubTask(task.getId());
                System.out.println("--> " + task);
            });
        });
        System.out.println("Подзадачи:");
        manager.getAllSubTasks().forEach(subtask -> {
            manager.getSubTask(subtask.getId());
            System.out.println(subtask);
        });

        System.out.println("История:");
        manager.getHistory().forEach(System.out::println);
    }
}