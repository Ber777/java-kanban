package manager;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ManagersTest {

    // Тесты4,5: утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров:
    @Test
    void shouldInitializeInMemoryTaskManagerGetDefault() {
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

    @Test
    void shouldInitializeInMemoryHistoryManagerGetDefaultHistory() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }
}