package manager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    // Тест6: InMemoryTaskManager добавляет задачи и может найти их по id:
    @Test
    void shouldAddNewTask() {
        final Long taskId = taskManager.add(new Task(1L, "Test6", "Test6 desc", Status.NEW));
        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(taskId, savedTask.getId(), "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskId, tasks.getFirst().getId(), "Задачи не совпадают.");
    }

    // Тест7: InMemoryTaskManager добавляет подзадачи и эпики, и может найти их по id:
    @Test
    void shouldAddNewEpicAndSubtasks() {
        //проверяем, что InMemoryTaskManager добавляет эпики и подзадачи и может найти их по id;
        final Long epicNum1 = taskManager.add(new Epic("Эпик 1", "Описание эпика 1"));
        final Long subTaskNum1 = taskManager.add(new SubTask("Первый сабтаск первого эпика", "", Status.NEW, epicNum1));
        final Long subTaskNum2 = taskManager.add(new SubTask("Второй сабтаск первого эпика", "", Status.NEW, epicNum1));
        final Long subTaskNum3 = taskManager.add(new SubTask("Третий сабтаск первого эпика", "", Status.NEW, epicNum1));

        final Epic epic = taskManager.getEpic(epicNum1);
        final SubTask subTask1 = taskManager.getSubTask(subTaskNum1);
        final SubTask subTask2 = taskManager.getSubTask(subTaskNum2);
        final SubTask subTask3 = taskManager.getSubTask(subTaskNum3);

        assertNotNull(epic, "Эпик не найден.");
        assertNotNull(subTask2, "Подзадача не найдена.");
        assertEquals(epicNum1, epic.getId(), "Эпики не совпадают.");
        assertEquals(subTaskNum1, subTask1.getId(), "Подзадачи не совпадают.");
        assertEquals(subTaskNum3, subTask3.getId(), "Подзадачи не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epicNum1, epics.getFirst().getId(), "Эпики не совпадают.");

        final List<SubTask> subtasks = taskManager.getAllSubTasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask1, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    // Тест8: Обновленная задача имеет тот же номер:
    @Test
    public void shouldReturnTaskWithTheSameIdUpdateTask() {
        final Task expectedTask = new Task(1L, "Задача 1", "Описание задачи 1");
        taskManager.add(expectedTask);
        final Task updatedTask = new Task(expectedTask.getId(), "Обновленная задача", "Обновленное описание", Status.IN_PROGRESS);
        taskManager.update(updatedTask);
        final Task actualEpic = taskManager.getTask(updatedTask.getId());
        assertEquals(expectedTask.getId(), actualEpic.getId(), "Вернулась задачи с другим id");
    }

    // Тест9: Обновленный эпик имеет тот же номер:
    @Test
    public void shouldReturnEpicWithTheSameIdUpdateEpic() {
        final Epic expectedEpic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.add(expectedEpic);
        final Epic updatedEpic = new Epic(expectedEpic.getId(), "Обновленный эпик", "Обновленное описание");
        taskManager.update(updatedEpic);
        final Epic actualEpic = taskManager.getEpic(updatedEpic.getId());
        assertEquals(expectedEpic.getId(), actualEpic.getId(), "Вернулся эпик с другим id");
    }

    // Тест10: Обновленная подзадача имеет тот же номер:
    @Test
    public void shouldReturnSubTaskWithTheSameIdUpdateSubTask() {
        final Long epicNum1 = taskManager.add(new Epic("Эпик 1", "Описание эпика 1"));
        final SubTask expectedSubTask = new SubTask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epicNum1);
        taskManager.add(expectedSubTask);
        final SubTask updatedSubTask = new SubTask(expectedSubTask.getId(), "Обновленная подзадача", "Обновленное описание", Status.IN_PROGRESS, epicNum1);
        taskManager.update(updatedSubTask);
        final SubTask actualSubTask = taskManager.getSubTask(updatedSubTask.getId());
        assertEquals(expectedSubTask.getId(), actualSubTask.getId(), "Вернулась подзадача с другим id");
    }

    // Тест 11: После удаления всех задач список должен быть пуст:
    @Test
    public void shouldReturnEmptyListDeleteTasks() {
        taskManager.add(new Task(1L, "Задача 1", "Описание 1"));
        taskManager.add(new Task(2L, "Задача 2", "Описание 2"));
        taskManager.deleteAllTasks();
        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "После удаления задач список должен быть пуст.");
    }

    // Тест 12: После удаления всех эпиков список должен быть пуст:
    @Test
    public void shouldReturnEmptyListDeleteEpics() {
        taskManager.add(new Epic("Эпик 1", "Описание 1"));
        taskManager.deleteAllEpics();
        List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty(), "После удаления эпиков список должен быть пуст.");
    }

    // Тест 13: После удаления всех подзадач список должен быть пуст:
    @Test
    public void shouldReturnEmptyListDeleteSubTasks() {
        final Long epicNum1 = taskManager.add(new Epic("Эпик 1", "Описание эпика 1"));
        final Long subTaskNum1 = taskManager.add(new SubTask("Первый сабтаск первого эпика", "", Status.NEW, epicNum1));
        final Long subTaskNum2 = taskManager.add(new SubTask("Второй сабтаск первого эпика", "", Status.NEW, epicNum1));
        final Long subTaskNum3 = taskManager.add(new SubTask("Третий сабтаск первого эпика", "", Status.NEW, epicNum1));
        taskManager.deleteAllSubTasks();
        List<SubTask> subtasks = taskManager.getAllSubTasks();
        assertTrue(subtasks.isEmpty(), "После удаления подзадач список должен быть пуст.");
    }

    // Тест 14: задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера:
    @Test
    void shouldHaveIdenticalFieldsTaskCreatedAndTaskAddedAutomatic() {
        Task expectedTask = new Task(1L, "Задача 1", "Описание 1", Status.NEW);
        taskManager.add(expectedTask);
        List<Task> list = taskManager.getAllTasks();
        Task actual = list.getFirst();
        assertEquals(expectedTask.getId(), actual.getId());
        assertEquals(expectedTask.getName(), actual.getName());
        assertEquals(expectedTask.getDescription(), actual.getDescription());
        assertEquals(expectedTask.getStatus(), actual.getStatus());
    }
}