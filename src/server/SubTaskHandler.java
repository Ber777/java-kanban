package server;

import exception.*;
import manager.*;
import model.*;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler {

    SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void apiGet(HttpExchange h) throws IOException {
        String[] components = getComponents(h);

        if (components.length == 2) {
            sendObject(h, taskManager.getAllSubTasks());
            return;
        }

        Optional<SubTask> subtask = Optional.empty();
        try {
            long id = Integer.parseInt(components[2]);
            subtask = Optional.ofNullable(taskManager.getSubTask(id));
        } catch (NumberFormatException ignored) {
        }

        if (subtask.isPresent())
            sendObject(h, subtask.get());
        else
            sendNotFound(h);
    }


    @Override
    protected void apiPost(HttpExchange h) throws IOException {
        String body = getRequestBody(h);
        SubTask subTask = null;
        try {
            subTask = gson.fromJson(body, SubTask.class);
        } catch (JsonSyntaxException e) {
            System.err.println(e.getMessage());
        }

        if (subTask == null) {
            sendBadRequest(h);
            return;
        }

        try {
            if (subTask.getId() < 1L) {
                taskManager.add(subTask);
            } else {
                taskManager.update(subTask);
            }
            sendAdded(h, subTask);
        } catch (TimeException e) {
            sendHasInteractions(h, e.getMessage());
        } catch (EpicNotFoundException e) {
            sendBadRequest(h);
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

        if (taskManager.deleteSubTask(id))
            sendText(h, "Подзадача успешно удалена.", STATUS_CODE_OK);
        else sendNotFound(h);
    }
}
