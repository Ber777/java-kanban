package server;

import manager.*;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void apiGet(HttpExchange h) throws IOException {
        sendObject(h, taskManager.getHistory());
    }
}