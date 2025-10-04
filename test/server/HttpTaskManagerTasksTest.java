package server;

import manager.*;
import model.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManagerTasksTest {
    HttpTaskServer server;
    protected final TaskManager manager = new InMemoryTaskManager();
    protected final Gson gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    public HttpTaskManagerTasksTest() throws IOException {
        server = new HttpTaskServer(manager);
    }

    @BeforeEach
    public void start() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubTasks();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void shouldReturnCode201AddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Task 1", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gsonBuilder.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldReturnCode201AddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String taskJson = gsonBuilder.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        ArrayList<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", tasksFromManager.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    public void shouldReturnCode201AddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.add(epic);
        SubTask subTask = new SubTask("SubTask 1", "Testing subTask 1", Status.NEW, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gsonBuilder.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        ArrayList<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("SubTask 1", tasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    void shouldReturnCode406TimeConflict() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.add(task1);
        Task task2 = new Task("Task 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(3));
        String taskJson = gsonBuilder.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldReturnCode200GetTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.add(task);

        HttpClient client = HttpClient.newHttpClient();
        String taskId = String.format("http://localhost:8080/tasks/%d", task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(taskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        String expectedBody = gsonBuilder.toJson(task);
        String actuallyBody = response.body();
        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    void shouldReturnCode200GetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.add(epic);

        HttpClient client = HttpClient.newHttpClient();
        String epicId = String.format("http://localhost:8080/epics/%d", epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        String expectedBody = gsonBuilder.toJson(epic);
        String actuallyBody = response.body();
        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    void shouldReturnCode200GetSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.add(epic);
        SubTask subTask = new SubTask("SubTask 1", "Testing subTask 1", Status.NEW, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.add(subTask);

        HttpClient client = HttpClient.newHttpClient();
        String subTaskId = String.format("http://localhost:8080/subtasks/%d", subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(subTaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        String expectedBody = gsonBuilder.toJson(subTask);
        String actuallyBody = response.body();
        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    void shouldReturnCode200DeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.add(epic);

        HttpClient client = HttpClient.newHttpClient();
        String epicId = String.format("http://localhost:8080/epics/%d", epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(epicId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        int expectedEpicsSize = 0;
        int actuallyEpicsSize = manager.getAllEpics().size();

        assertEquals(expectedEpicsSize, actuallyEpicsSize, "Менеджер задач не пуст");
    }

    @Test
    void shouldReturnCode200DeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1");
        manager.add(task);

        HttpClient client = HttpClient.newHttpClient();
        String taskId = String.format("http://localhost:8080/tasks/%d", task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        int expectedEpicsSize = 0;
        int actuallyEpicsSize = manager.getAllTasks().size();

        assertEquals(expectedEpicsSize, actuallyEpicsSize, "Менеджер задач не пуст");
    }

    @Test
    void shouldReturnCode200DeleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.add(epic);
        SubTask subTask = new SubTask("SubTask 1", "Testing subTask 1", Status.NEW, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.add(subTask);

        HttpClient client = HttpClient.newHttpClient();
        String taskId = String.format("http://localhost:8080/subtasks/%d", subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        int expectedEpicsSize = 0;
        int actuallyEpicsSize = manager.getAllSubTasks().size();

        assertEquals(expectedEpicsSize, actuallyEpicsSize, "Менеджер задач не пуст");
    }

    @Test
    void shouldReturnCode201UpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1");
        manager.add(task1);
        Task task2 = new Task(task1.getId(), "Task 1 Rename", "Testing task 1");
        String json = gsonBuilder.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус код должен быть 201");

        int expectedSubTasksSize = 1;
        String expectedSubtasksName = "Task 1 Rename";
        int actuallySubTasksSize = manager.getAllTasks().size();
        String actuallySubtaskName = manager.getAllTasks().stream().findFirst().map(Task::getName).get();

        assertEquals(expectedSubTasksSize, actuallySubTasksSize, "Некорректное количество задач");
        assertEquals(expectedSubtasksName, actuallySubtaskName, "Некорректное имя задачи");
    }

    @Test
    void shouldReturnCode201UpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        manager.add(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "Testing subTask 2", Status.NEW, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask subTask2 = new SubTask(subTask1.getId(), "Подзадача 1", "Первый сабтаск первого эпика",
                Status.DONE, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        String json = gsonBuilder.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус код должен быть 201");

        int expectedSubTasksSize = 1;
        String expectedSubtasksName = "Подзадача 1";
        int actuallySubTasksSize = manager.getAllSubTasks().size();
        String actuallySubtaskName = manager.getAllSubTasks().stream().findFirst().map(SubTask::getName).get();

        assertEquals(expectedSubTasksSize, actuallySubTasksSize, "Некорректное количество подзадач");
        assertEquals(expectedSubtasksName, actuallySubtaskName, "Некорректное имя подзадачи");
    }

    @Test
    void shouldReturnCode201UpdateEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1");
        manager.add(epic1);
        Epic epic2 = new Epic(epic1.getId(), "Epic 1 Rename", "Testing epic 2");
        String json = gsonBuilder.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус код должен быть 201");

        int expectedSubTasksSize = 1;
        String expectedSubtasksName = "Epic 1 Rename";
        int actuallySubTasksSize = manager.getAllEpics().size();
        String actuallySubtaskName = manager.getAllEpics().stream().findFirst().map(Epic::getName).get();

        assertEquals(expectedSubTasksSize, actuallySubTasksSize, "Некорректное количество эпиков");
        assertEquals(expectedSubtasksName, actuallySubtaskName, "Некорректное имя эпика");
    }

    @Test
    void shouldReturnCode404TaskNotFound() throws IOException, InterruptedException {
        Task task = new Task(100L, "Task 1", "Testing task 1");
        manager.add(task);
        long id = 404L;

        HttpClient client = HttpClient.newHttpClient();
        String taskId = String.format("http://localhost:8080/tasks/%d", id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        int expectedSubTasksSize = 1;
        int actuallySubTasksSize = manager.getAllTasks().size();
        assertEquals(expectedSubTasksSize, actuallySubTasksSize, "Некорректное количество задач");
    }

    @Test
    void shouldReturnCode200GetHistory() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1");
        manager.add(task);

        Epic epic1 = new Epic("Epic 1", "Testing epic 1");
        manager.add(epic1);

        SubTask subTask = new SubTask("SubTask 1", "Testing subTask 1", Status.NEW, epic1.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.add(subTask);

        manager.getSubTask(subTask.getId());
        manager.getTask(task.getId());
        manager.getEpic(epic1.getId());
        manager.getSubTask(subTask.getId());
        manager.getTask(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        String expectedBodyTask = gsonBuilder.toJson(task);
        String expectedBodyEpic = gsonBuilder.toJson(epic1);
        String expectedBodySubTask = gsonBuilder.toJson(subTask);
        String expectedBody = String.format("[%s,%s,%s]", expectedBodyEpic, expectedBodySubTask, expectedBodyTask);
        String actuallyBody = response.body();

        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    void shouldReturnCode200GetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 1",
                LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(5));
        manager.add(task);

        Epic epic = new Epic("Epic 1", "Testing epic 1",
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.add(epic);

        SubTask subTask = new SubTask("SubTask 2", "Testing subTask 2", Status.NEW, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.add(subTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код должен быть 200");

        String expectedBodyTask = gsonBuilder.toJson(task);
        String expectedBodySubTask = gsonBuilder.toJson(subTask);
        String expectedBody = String.format("[%s,%s]", expectedBodySubTask, expectedBodyTask);
        String actuallyBody = response.body();

        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }
}