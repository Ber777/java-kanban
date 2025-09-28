package manager;

import java.io.File;

// Утилитарный класс
public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }
}
