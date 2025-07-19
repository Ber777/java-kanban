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

        //Изменяем статусы объектов:
        taskManager.update(new Task(task1, "Задача 1", "Первый обычный таск", Status.IN_PROGRESS));
        taskManager.update(new SubTask(subTask1, "Первый сабтаск первого эпика", "Подзадача 1", Status.IN_PROGRESS, epicNum1));
        taskManager.update(new SubTask(subTask2, "Второй сабтаск второго эпика", "Подзадача 2", Status.DONE,epicNum1));
        System.out.println("Эпики:");
        taskManager.printAllEpics();
        taskManager.update(new Epic(epicNum1, "Эпик 1", "Переименованный первый эпик"));
        taskManager.update(new SubTask(subTask3, "Первый сабтаск второго эпика", "Подзадача 1", Status.DONE, epicNum2));
        subTask1 = taskManager.add(new SubTask("Первый сабтаск первого эпика", "", Status.DONE, epicNum1));
        subTask2 = taskManager.add(new SubTask("Второй сабтаск первого эпика", "", Status.DONE, epicNum1));

        // Печать списков после обновления:
        System.out.println("Печать списков после обновления:");
        System.out.println("Задачи:");
        taskManager.printAllTasks();
        System.out.println("Подзадачи:");
        taskManager.printAllSubTasks();
        System.out.println("Эпики:");
        taskManager.printAllEpics();
        taskManager.printAllSubTasksOfEpic(epicNum1);

        // Удаление:
        System.out.println("Удаление:");
        taskManager.deleteTask(task1);
        taskManager.deleteSubTask(subTask2);
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
