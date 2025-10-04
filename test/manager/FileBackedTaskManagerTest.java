package manager;

import model.*;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

class FileBackedTaskManagerTest extends TaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void testSaveAndLoadEmptyManager() {
        // Сохраняем пустой менеджер
        manager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что все списки пусты
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubTasks().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }


    @Test
    void testSaveAndLoadTasks() {
        // Создаем задачи
        Task task = new Task("Test Task", "Description");
        Long taskId = manager.add(task);

        Epic epic = new Epic("Test Epic", "Description");
        Long epicId = manager.add(epic);

        SubTask subTask = new SubTask("Test SubTask", "Description", Status.NEW, epicId);
        Long subTaskId = manager.add(subTask);
        //SubTask subTask2 = new SubTask("Test SubTask2", "Description2", Status.NEW, epicId);
        //Long subTaskId2 = manager.add(subTask2);

        // Сохраняем и загружаем
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем восстановление задач
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubTasks().size());

        // Проверяем корректность данных
        Task loadedTask = loadedManager.getTask(taskId);
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());

        // Проверяем, что подзадача связана с эпиком
        SubTask loadedSubTask = loadedManager.getSubTask(subTaskId);
        assertEquals(epicId, loadedSubTask.getEpicIdSub());

        Epic loadedEpic = loadedManager.getEpic(epicId);
        assertTrue(loadedEpic.getSubTask().contains(subTaskId));
    }
}