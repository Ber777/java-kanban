package manager;

import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    // Тест 15: список истории должен содержать максимум 10 задач:
    // Тест устарел - теперь неограниченно кол-во задач в истории:
    /*
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
    */

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

    // Новые тесты:
    @Test
    void shouldAddTasksToHistoryList() {
        Task task1 = new Task("Задача 1", "Описание 1");
        Long taskNum1 = taskManager.add(task1);
        taskManager.getTask(taskNum1);
        Task task2 = new Task("Задача 2", "Описание 2");
        Long taskNum2 = taskManager.add(task2);
        taskManager.getTask(taskNum2);
        assertEquals(List.of(task1, task2), taskManager.getHistory());
    }

    @Test
    void shouldReturnAnEmptyListWhenThereIsNoHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Задача 1", "Описание 1");
        Long taskNum1 = taskManager.add(task1);
        taskManager.getTask(taskNum1);
        Task task2 = new Task("Задача 2", "Описание 2");
        Long taskNum2 = taskManager.add(task2);
        taskManager.getTask(taskNum2);
        taskManager.deleteTask(taskNum1);

        assertEquals(List.of(task2), taskManager.getHistory());
    }

    @Test
    void shouldNotAddExistingTaskToHistoryList() {
        Task task1 = new Task("Задача 1", "Описание 1");
        Long taskNum1 = taskManager.add(task1);
        taskManager.getTask(taskNum1);
        taskManager.getTask(taskNum1);

        assertEquals(List.of(task1), taskManager.getHistory());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void shouldRemoveSecondTaskFromHistoryList() {
        Task task1 = new Task("Задача 1", "Описание 1");
        Long taskNum1 = taskManager.add(task1);
        taskManager.getTask(taskNum1);
        Task task2 = new Task("Задача 2", "Описание 2");
        Long taskNum2 = taskManager.add(task2);
        taskManager.getTask(taskNum2);
        Task task3 = new Task("Задача 3", "Описание 3");
        Long taskNum3 = taskManager.add(task3);
        taskManager.getTask(taskNum3);
        taskManager.deleteTask(taskNum2);

        assertEquals(List.of(task1, task3), taskManager.getHistory());
    }

    @Test
    void shouldReturnEmptyHistoryList() {
        taskManager.getTask(0L);
        taskManager.getEpic(0L);
        taskManager.getSubTask(0L);

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldDeleteSubTasksFromHistoryListAfterDeleteEpic() {
        Long epicNum1 = taskManager.add(new Epic("Эпик 1", "Мой первый эпик"));
        Long subTask1 = taskManager.add(new SubTask("Первый сабтаск первого эпика", "", Status.NEW, epicNum1));
        Long subTask2 = taskManager.add(new SubTask("Второй сабтаск первого эпика", "", Status.NEW, epicNum1));
        taskManager.getEpic(epicNum1);
        taskManager.getSubTask(subTask1);
        taskManager.getSubTask(subTask2);
        taskManager.deleteEpic(epicNum1);

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnOnlyOneTaskFromHistoryListAfterDeleteEpic() {
        Task task1 = new Task("Задача 1", "Описание 1");
        Long taskNum1 = taskManager.add(task1);
        taskManager.getTask(taskNum1);

        Long epicNum1 = taskManager.add(new Epic("Эпик 1", "Мой первый эпик"));
        Long subTask1 = taskManager.add(new SubTask("Первый сабтаск первого эпика", "", Status.NEW, epicNum1));
        Long subTask2 = taskManager.add(new SubTask("Второй сабтаск первого эпика", "", Status.NEW, epicNum1));
        taskManager.getEpic(epicNum1);
        taskManager.getSubTask(subTask1);
        taskManager.getSubTask(subTask2);
        taskManager.deleteEpic(epicNum1);

        assertEquals(List.of(task1), taskManager.getHistory());
        assertEquals(1, taskManager.getHistory().size());
    }
}