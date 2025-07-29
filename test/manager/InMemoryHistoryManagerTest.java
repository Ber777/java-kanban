package manager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Хотел бы оставить комментарий:
// По теории из спринта 4: "Ч"аще всего статические методы применяются в утилитарных задачах".
// Класс Managers как раз является утилитарным классом, поэтому оба его метода сделал статичными,
// но это приводит к тому, что одновременный запуск тестов в данном классе (InMemoryHistoryManagerTest)
// приводит к ошибкам, поскольку статическую переменную taskManager мы меняем между тестами.
// По отдельности каждый тест заканчивается успешно.
// Попробовал дополнительно погрузиться в данный вопрос: пробовал действовать через нотацию AfterEach,
// несколько объектов TaskManager, но не добился успешного запуск одновременно тестов.
// Есть способ как-то все же обойти подобное поведение или только остается не использовать статичные методы/переменные?

class InMemoryHistoryManagerTest {
    private static TaskManager taskManager;
    //private TaskManager taskManager1;  // См. комментарий выше

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        //taskManager1 = Managers.getDefault();
    }

    // Не сработало, см. комментарий выше
    /*@AfterEach
    public void afterEach() {
        taskManager = null;
        //taskManager1 = null;
    }*/

    // Тест 15: список истории должен содержать максимум 10 задач:
    @Test
    public void shouldReturnListOf10TasksGetHistory() {
        for (int i = 0; i < 11; i++) {
            taskManager.add(new Task("Задача №" + (i+1), "Описание " + (i+1)));
        }

        List<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            taskManager.getTask(task.getId());
        }

        List<Task> list = taskManager.getHistory();
        assertEquals(10, list.size(), "Неверное количество элементов в истории ");
    }

    // Тесты 16-18: задачи, подзадачи и эпики, добавляемые в HistoryManager, сохраняют предыдущую версию себя и ее данных.
    @Test
    public void shouldReturnOldTaskAfterUpdateGetHistory() {
        Task task1 = new Task("Задача 1", "Описание 1");
        taskManager.add(task1);
        taskManager.getTask(task1.getId());
        taskManager.update(new Task(task1.getId(), "Обновленная задача 1", "Обновленное описание задачи 1", Status.DONE));
        List<Task> tasks = taskManager.getHistory();
        Task oldTask = tasks.getFirst();  // tasks.get(0);
        assertEquals(task1.getName(), oldTask.getName(), "В истории не сохранилась старая версия задачи");
        assertEquals(task1.getDescription(), oldTask.getDescription(), "В истории не сохранилась старая версия задачи");
    }

    @Test
    public void shouldReturnOldEpicAfterUpdateGetHistory() {
        final Long epicNum1 = taskManager.add(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = taskManager.getEpic(epicNum1);
        taskManager.update(new Epic(epicNum1, "Обновленный эпик 1", "Обновленное описание эпика 1"));
        List<Task> epics = taskManager.getHistory();
        Epic oldEpic = (Epic) epics.getFirst();
        assertEquals(epic.getName(), oldEpic.getName(), "В истории не сохранилась старая версия эпика");
        assertEquals(epic.getDescription(), oldEpic.getDescription(), "В истории не сохранилась старая версия эпика");
    }

    @Test
    public void shouldReturnOldSubtaskAfterUpdateGetHistory() {
        final Long epicNum1 = taskManager.add(new Epic("Эпик 1", "Описание эпика 1"));
        final Long subTaskNum1 = taskManager.add(new SubTask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epicNum1));
        SubTask subTask = taskManager.getSubTask(subTaskNum1);
        taskManager.update(new SubTask(subTaskNum1, "Обновленная подзадача 1", "Обновленное описание 1", Status.IN_PROGRESS, epicNum1));
        List<Task> subtasks = taskManager.getHistory();
        SubTask oldSubTask = (SubTask) subtasks.getFirst();
        assertEquals(subTask.getName(), oldSubTask.getName(), "В истории не сохранилась старая версия подзадачи");
        assertEquals(subTask.getDescription(), oldSubTask.getDescription(), "В истории не сохранилась старая версия подзадачи");
    }
}