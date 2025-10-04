package server;

import manager.TaskManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {

    public static final int STATUS_CODE_OK = 200;
    public static final int STATUS_CODE_ADDED = 201;
    private static final int STATUS_CODE_BAD_REQUEST = 400;
    public static final int STATUS_CODE_NOT_FOUND = 404;
    private static final int STATUS_CODE_NOT_ACCEPTABLE = 406;

    protected final TaskManager taskManager;
    protected static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gson = gsonBuilder.create();
    }

    BaseHttpHandler(TaskManager manager) {
        taskManager = manager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        ApiMethod method = ApiMethod.valueOf(h.getRequestMethod());
        switch (method) {
            case ApiMethod.GET : apiGet(h); break;
            case ApiMethod.POST : apiPost(h); break;
            case ApiMethod.DELETE : apiDelete(h); break;
            default: sendBadRequest(h);
        }
    }

    protected void apiGet(HttpExchange h) throws IOException {
        sendBadRequest(h);
    }

    protected void apiPost(HttpExchange h) throws IOException {
        sendBadRequest(h);
    }

    protected void apiDelete(HttpExchange h) throws IOException {
        sendBadRequest(h);
    }


    protected void sendObject(HttpExchange h, Object responseObject) throws IOException {
        sendObject(h, responseObject, STATUS_CODE_OK);
    }

    protected void sendAdded(HttpExchange h, Object responseObject) throws IOException {
        sendObject(h, responseObject, STATUS_CODE_ADDED);
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(STATUS_CODE_NOT_FOUND, 0);
        h.close();
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        h.sendResponseHeaders(STATUS_CODE_BAD_REQUEST, 0);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String message) throws IOException {
        sendText(h, message, STATUS_CODE_NOT_ACCEPTABLE);
    }

    private void sendObject(HttpExchange h, Object responseObject, int code) throws IOException {
        if (responseObject == null)
            throw new IllegalArgumentException();

        try {
            String responseText = gson.toJson(responseObject);
            sendText(h, responseText, code);
        } catch (JsonIOException e) {
            throw new RuntimeException();
        }
    }

    protected void sendText(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }


    protected String[] getComponents(HttpExchange h) {
        URI requestURI = h.getRequestURI();
        String path = requestURI.getPath();
        return path.split("/");
    }

    protected String getRequestBody(HttpExchange h) throws IOException {
        InputStream inputStream = h.getRequestBody();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
