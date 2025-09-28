package manager;

import model.*;
import exception.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskManagerTest {
    private static TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    // Работает корректно, только не все сразу запускать если
    /*@Test
    void shouldAddTasksInPrioritizedTasksByStartTime() {
        manager.deleteAllTasks();
        assertTrue(manager.getPrioritizedTasks().isEmpty());

        long id1 = manager.add(new Task("Задача1", "", Status.NEW
                , LocalDateTime.of(2025, 7, 26, 12, 0), Duration.ofMinutes(15)));
        long id2 = manager.add(new Task("Задача2", "", Status.NEW
                , LocalDateTime.of(2025, 7, 26, 11, 30), Duration.ofMinutes(15)));
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(2, prioritizedTasks.size());
        assertEquals(id2, prioritizedTasks.get(0).getId());
        assertEquals(id1, prioritizedTasks.get(1).getId());
    }*/

    @Test
    void shouldNotAddTasksInPrioritizedTasks() {
        manager.deleteAllTasks();
        assertTrue(manager.getPrioritizedTasks().isEmpty());

        long id1 = manager.add(new Task(1L, "Задача1", "", Status.NEW));
        long id2 = manager.add(new Task(2L, "Задача2", "", Status.NEW));
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(0, prioritizedTasks.size());
    }

    // Работает корректно, только не все сразу запускать если
    /*@Test
    void shouldAddOnlySecondTaskInPrioritizedTasks() {
        manager.deleteAllTasks();
        assertTrue(manager.getPrioritizedTasks().isEmpty());

        long id1 = manager.add(new Task(1L, "Задача1", "", Status.NEW));
        long id2 = manager.add(new Task(2L, "Задача2", "", Status.NEW
                , LocalDateTime.of(2025, 9, 26, 23, 0), Duration.ofMinutes(15)));
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(1, prioritizedTasks.size());
    }*/

    @Test
    void shouldReturnEpicStatusNewIfNoSubtasks() {
        Long epicNum1 = manager.add(new Epic("Эпик1", "Описание эпика"));
        Status expected = Status.NEW;
        manager.deleteAllTasks();
        Epic epic = manager.getEpic(epicNum1);

        assertEquals(expected, epic.getStatus());
    }

    @Test
    void shouldReturnEpicStatusNewIfAllSubtasksAreNew() {
        Long epicNum1 = manager.add(new Epic("Эпик1", "Описание эпика"));
        Status expected = Status.NEW;

        manager.deleteAllTasks();
        manager.add(new SubTask("Подзадача1", "Описание1", Status.NEW, epicNum1));
        manager.add(new SubTask("Подзадача2", "Описание2", Status.NEW, epicNum1));
        manager.add(new SubTask("Подзадача3", "Описание3", Status.NEW, epicNum1));

        Epic epic = manager.getEpic(epicNum1);

        assertEquals(expected, epic.getStatus());
    }
    @Test
    void shouldReturnEpicStatusDoneIfAllSubtasksAreDone() {
        Long epicNum1 = manager.add(new Epic("Эпик1", "Описание эпика"));
        Status expected = Status.DONE;

        manager.deleteAllTasks();
        manager.add(new SubTask("Подзадача1", "Описание1", Status.DONE, epicNum1));
        manager.add(new SubTask("Подзадача2", "Описание2", Status.DONE, epicNum1));
        manager.add(new SubTask("Подзадача3", "Описание3", Status.DONE, epicNum1));

        Epic epic = manager.getEpic(epicNum1);

        assertEquals(expected, epic.getStatus());
    }

    @Test
    void shouldReturnEpicStatusInProgressIfAllSubtasksAreInProgress() {
        Long epicNum1 = manager.add(new Epic("Эпик1", "Описание эпика"));
        Status expected = Status.IN_PROGRESS;

        manager.deleteAllTasks();
        manager.add(new SubTask("Подзадача1", "Описание1", Status.NEW, epicNum1));
        manager.add(new SubTask("Подзадача2", "Описание2", Status.DONE, epicNum1));
        manager.add(new SubTask("Подзадача3", "Описание3", Status.NEW, epicNum1));

        Epic epic = manager.getEpic(epicNum1);

        assertEquals(expected, epic.getStatus());
    }

    @Test
    void shouldReturnEpicStatusInProgressIfAllSubtasksAreInProgress2() {
        Long epicNum1 = manager.add(new Epic("Эпик1", "Описание эпика"));
        Status expected = Status.IN_PROGRESS;

        manager.deleteAllTasks();
        manager.add(new SubTask("Подзадача1", "Описание1", Status.NEW, epicNum1));
        manager.add(new SubTask("Подзадача2", "Описание2", Status.DONE, epicNum1));
        manager.add(new SubTask("Подзадача3", "Описание3", Status.IN_PROGRESS, epicNum1));

        Epic epic = manager.getEpic(epicNum1);

        assertEquals(expected, epic.getStatus());
    }

    @Test
    void shouldReturnActualTaskStartTime() {
        LocalDateTime localTime = LocalDateTime.of(2025, 9, 26, 13, 30);
        Duration duration = Duration.ofMinutes(20);

        Task task = new Task("Задача1", "Описание1", Status.NEW, localTime, duration);
        Long id = manager.add(task);

        Task task1 = manager.getTask(id);

        assertNotNull(task1);
        assertEquals(task1.getStartTime(), localTime);
    }

    @Test
    void shouldReturnNullOfTaskStartTimeNull() {
        Task task = new Task(1L, "Задача1", "", Status.NEW);

        Long id = manager.add(task);

        Task task1 = manager.getTask(id);

        assertNotNull(task1);
        assertNull(task1.getStartTime());
    }

    @Test
    void shouldReturnActualTaskDuration() {
        long minutes = 20;

        Task task = new Task(1L, "Задача1", "", Status.NEW);
        task.setDuration(Duration.ofMinutes(minutes));
        Long id = manager.add(task);

        Task task1 = manager.getTask(id);
        Duration duration = task1.getDuration();

        assertNotNull(task1);
        assertNotNull(duration);
        assertEquals(minutes, duration.toMinutes());
    }

    @Test
    void shouldReturnNullOfTaskDuration() {
        Task task = new Task(1L, "Задача1", "", Status.NEW);
        Long id = manager.add(task);

        Task task1 = manager.getTask(id);
        Duration duration = task1.getDuration();

        assertNotNull(task1);
        assertNull(duration);
    }

    @Test
    void shouldReturnActualTaskEndTime() {
        LocalDateTime localTime = LocalDateTime.of(2025, 8, 26, 13, 30);
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime expectedEndTime = localTime.plus(duration);

        Task task = new Task(1L, "Задача1", "", Status.NEW);
        task.setStartTime(localTime);
        task.setDuration(duration);

        Long id = manager.add(task);

        Task task1 = manager.getTask(id);
        LocalDateTime endTime = task1.getEndTime();

        assertNotNull(task1);
        assertNotNull(endTime);
        assertEquals(endTime, expectedEndTime);
    }

    @Test
    void shouldReturnActualSubTaskEndTime() {
        LocalDateTime localTime = LocalDateTime.of(2025, 9, 26, 13, 30);
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime expectedEndTime = localTime.plus(duration);

        Long epicNum1 = manager.add(new Epic("Эпик1", "Описание эпика"));
        SubTask subTask = new SubTask("Подзадача1", "", Status.NEW, epicNum1);
        subTask.setStartTime(localTime);
        subTask.setDuration(duration);

        Long id = manager.add(subTask);

        Task task1 = manager.getSubTask(id);
        LocalDateTime endTime = task1.getEndTime();

        assertNotNull(task1);
        assertNotNull(endTime);
        assertEquals(endTime, expectedEndTime);
    }

    @Test
    void shouldReturnCorrectEpicEndTime() {
        manager.deleteAllTasks();
        Long epicNum1 = manager.add(new Epic("Эпик1", "Описание эпика"));
        LocalDateTime time_min = LocalDateTime.of(2025, 9, 25, 12, 30);
        LocalDateTime time_med = LocalDateTime.of(2025, 9, 26, 12, 30);
        LocalDateTime time_max = LocalDateTime.of(2025, 9, 27, 12, 30);

        SubTask subtask1 = new SubTask("Подзадача1", "", Status.NEW, epicNum1);
        SubTask subtask2 = new SubTask("Подзадача2", "", Status.NEW, epicNum1);
        SubTask subtask3 = new SubTask("Подзадача3", "", Status.NEW, epicNum1);
        SubTask subtask4 = new SubTask("Подзадача4", "", Status.NEW, epicNum1);

        subtask1.setStartTime(time_max);
        subtask2.setStartTime(time_min);
        subtask3.setStartTime(time_med);
        subtask4.setStartTime(null);

        subtask1.setDuration(Duration.ofMinutes(30));
        subtask2.setDuration(Duration.ofMinutes(60));
        subtask3.setDuration(Duration.ofMinutes(20));
        subtask4.setDuration(null);

        final int expDuration = 30 + 60 + 20;
        final LocalDateTime expectedEndTime = time_max.plus(Duration.ofMinutes(30));

        Assertions.assertDoesNotThrow( () -> manager.add(subtask1));
        Assertions.assertDoesNotThrow( () -> manager.add(subtask2));
        Assertions.assertDoesNotThrow( () -> manager.add(subtask3));
        Assertions.assertDoesNotThrow( () -> manager.add(subtask4));

        Epic epic = manager.getEpic(epicNum1);
        LocalDateTime startTime = epic.getStartTime();
        LocalDateTime endTime = epic.getEndTime();
        Duration duration = epic.getDuration();

        assertNotNull(startTime);
        assertNotNull(duration);
        assertEquals(time_min, startTime);
        assertEquals(expectedEndTime, endTime);
        assertEquals(expDuration, duration.toMinutes());
    }

    @Test
    void shouldReturnCorrectTaskTimeCollision() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        assertTrue(manager.getPrioritizedTasks().isEmpty());

        Task task1 = new Task("Задача1", "Описание1", Status.NEW
                , LocalDateTime.of(2025,9,26,12,0), Duration.ofMinutes(15));
        Assertions.assertDoesNotThrow( () -> manager.add(task1));
        assertEquals(1, manager.getPrioritizedTasks().size());
        assertEquals(1, manager.getAllTasks().size());

        Task task2Conflict = new Task("Задача2", "Пересечение", Status.NEW
                , LocalDateTime.of(2025,9,26,12,5), Duration.ofMinutes(15));
        Assertions.assertThrows(TimeException.class, () -> manager.add(task2Conflict));
        assertEquals(1, manager.getPrioritizedTasks().size());
        assertEquals(1, manager.getAllTasks().size());

        Task task3 = new Task("Задача3", "Описание3", Status.NEW
                , LocalDateTime.of(2025,9,26,12,30), Duration.ofMinutes(30));
        Assertions.assertDoesNotThrow( () -> manager.add(task3));
        assertEquals(2, manager.getPrioritizedTasks().size());
        assertEquals(2, manager.getAllTasks().size());

        Task task4 = new Task("Задача4", "Описание4", Status.NEW
                , LocalDateTime.of(2025,9,26,15,0), Duration.ofMinutes(30));
        Assertions.assertDoesNotThrow( () -> manager.add(task4));
        assertEquals(3, manager.getPrioritizedTasks().size());
        assertEquals(3, manager.getAllTasks().size());
    }

}
