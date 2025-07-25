import model.*;
import manager.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        //Создаем две задачи:
        Long task1 = taskManager.add(new Task(1L, "Задача 1", "Первый обычный таск", Status.NEW));
        Long task2 = taskManager.add(new Task(2L, "Задача 2", "Второй обычный таск", Status.NEW));

        //Создаем эпик с двумя новыми сабтасками:
        Long epicNum1 = taskManager.add(new Epic("Эпик 1", "Мой первый эпик"));
        Long subTask1 = taskManager.add(new SubTask("Первый сабтаск первого эпика", "", Status.NEW, epicNum1));
        Long subTask2 = taskManager.add(new SubTask("Второй сабтаск первого эпика", "", Status.NEW, epicNum1));

        //Создаем второй эпик с одной сабтаской:
        Long epicNum2 = taskManager.add(new Epic("Эпик 2", "Мой второй эпик"));
        Long subTask3 = taskManager.add(new SubTask("Первый сабтаск второго эпика", "", Status.NEW, epicNum2));

        // Печать списков:
        System.out.println("Задачи:");
        taskManager.printAllTasks();
        System.out.println("Подзадачи:");
        taskManager.printAllSubTasks();
        System.out.println("Эпики:");
        taskManager.printAllEpics();
        //System.out.println(taskManager.getAllSubTasksOfEpic(epicNum1));
        taskManager.printAllSubTasksOfEpic(epicNum1);
        System.out.println();

        //Изменяем статусы объектов:
        System.out.println("Обновление:");
        taskManager.update(new Task(task1, "Задача 1", "Первый обычный таск", Status.IN_PROGRESS));
        System.out.println("Задачи после обновления:");
        taskManager.printAllTasks();
        taskManager.update(new SubTask(subTask1, "Первый сабтаск первого эпика", "Подзадача 1", Status.IN_PROGRESS, epicNum1));
        taskManager.update(new SubTask(subTask2, "Второй сабтаск первого эпика", "Подзадача 2", Status.DONE,epicNum1));
        taskManager.update(new SubTask(subTask3, "Первый сабтаск второго эпика", "Подзадача 1", Status.DONE,epicNum2));
        System.out.println("Подзадачи после обновления:");
        taskManager.printAllSubTasks();
        System.out.println("Эпики после обновления подзадач:");
        taskManager.printAllEpics();
        taskManager.update(new Epic(epicNum1, "Эпик 1", "Переименованный первый эпик"));
        System.out.println("Эпики после обновления:");
        taskManager.printAllEpics();
        taskManager.printAllSubTasksOfEpic(epicNum1);
        System.out.println();

        // Удаление:
        System.out.println("Удаление:");
        taskManager.deleteTask(task1);
        taskManager.deleteSubTask(subTask1);
        taskManager.deleteEpic(epicNum2);

        // Печать списков после удаления:
        System.out.println("Печать списков после удаления:");
        System.out.println("Задачи:");
        taskManager.printAllTasks();
        System.out.println("Подзадачи:");
        taskManager.printAllSubTasks();
        System.out.println("Эпики:");
        taskManager.printAllEpics();
        taskManager.printAllSubTasksOfEpic(epicNum1);
    }
}
