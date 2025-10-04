package server;

import manager.*;
import model.*;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void apiGet(HttpExchange h) throws IOException {
        String[] components = getComponents(h);

        if (components.length == 2) {
            sendObject(h, taskManager.getAllEpics());
            return;
        }

        long id = -1L;
        try {
            id = Integer.parseInt(components[2]);
        } catch (NumberFormatException ignored) {
        }

        Optional<Epic> epic = Optional.ofNullable(taskManager.getEpic(id));
        if (epic.isEmpty()) {
            sendNotFound(h);
            return;
        }

        if (components.length == 3) {
            sendObject(h, epic.get());
        } else if (components.length == 4 && "subtasks".equals(components[3])) {
            sendObject(h, taskManager.getAllSubTasksOfEpic(id));
        } else
            sendBadRequest(h);
    }


    @Override
    protected void apiPost(HttpExchange h) throws IOException {
        String body = getRequestBody(h);
        Epic epic = null;
        try {
            epic = gson.fromJson(body, Epic.class);
        } catch (JsonSyntaxException e) {
            System.err.println(e.getMessage());
        }

        if (epic == null) {
            sendBadRequest(h);
            return;
        }

        if (epic.getId() < 1L) {
            taskManager.add(epic);
        } else {
            taskManager.update(epic);
        }
        sendAdded(h, epic);
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
        } catch (NumberFormatException ignored) {
        }

        if (taskManager.deleteEpic(id))
            sendText(h, "Эпик успешно удален.", STATUS_CODE_OK);
        else sendNotFound(h);
    }
}
