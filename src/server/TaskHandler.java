package server;

import exception.TimeException;
import manager.TaskManager;
import model.Task;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void apiGet(HttpExchange h) throws IOException {
        String[] components = getComponents(h);

        if (components.length <= 2) {
            sendObject(h, taskManager.getAllTasks());
        } else {
            Task task = null;
            try {
                long id = Integer.parseInt(components[2]);
                task = taskManager.getTask(id);
            } catch (NumberFormatException ignored) {
            }

            if (task != null)
                sendObject(h, task);
            else
                sendNotFound(h);
        }
    }

    @Override
    protected void apiPost(HttpExchange h) throws IOException {
        String body = getRequestBody(h);
        Task task = null;
        try {
            task = gson.fromJson(body, Task.class);
        } catch (JsonSyntaxException e) {
            System.err.println(e.getMessage());
        }

        if (task == null) {
            sendBadRequest(h);
            return;
        }

        try {
            if (task.getId() == 0) {
                sendAdded(h, taskManager.add(task));
            } else {
                taskManager.update(task);
            }
            sendAdded(h, task);
        } catch (TimeException e) {
            sendHasInteractions(h, e.getMessage());
        }
    }

    @Override
    protected void apiDelete(HttpExchange h) throws IOException {
        String[] components = getComponents(h);

        if (components.length < 3) {
            sendBadRequest(h);
            return;
        }

        long id = -1L;
        try {
            id = Integer.parseInt(components[2]);
        } catch (NumberFormatException ignored) { }

        if (taskManager.deleteTask(id))
            sendText(h, "Задача успешно удалена.", STATUS_CODE_OK);
        else sendNotFound(h);
    }
}