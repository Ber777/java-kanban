package manager;

// Утилитарный класс (см. комментарий в InMemoryHistoryManagerTest:
public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
