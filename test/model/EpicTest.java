package model;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {
    Epic epic = null;

    @BeforeEach
    void beforeEach() {
        epic = new Epic(1L, "name", "description");
        epic.setStartTime(LocalDateTime.of(2025, 9, 26, 12, 0));
        epic.setDuration(Duration.ofMinutes(20));
    }

    // Тест2: экземпляры класса Epic (наследник класса Task) равны друг другу, если равен их id:
    @Test
    public void shouldReturnEqualsEpics() {
        Epic epic1 = new Epic(1L, "Эпик 1", "Мой первый эпик");
        Epic epic2 = new Epic(1L, "Эпик 2", "Мой второй эпик");
        Assertions.assertEquals(epic1, epic2,"Ошибка! Наследники класса Task должны быть равны друг другу, если равен их id;");
    }

    @Test
    void shouldReturnNotEquals1() {
        long id = epic.getId() + 1;
        Epic otherEpic = new Epic(id, "name", "description");
        otherEpic.setStartTime(LocalDateTime.of(2025, 9, 26, 12, 20));
        otherEpic.setDuration(Duration.ofMinutes(20));

        assertNotEquals(epic, otherEpic);
    }

    @Test
    void shouldReturnNotEquals2() {
        long id = epic.getId() + 1;
        Epic otherEpic = new Epic(id, "name", "description");
        otherEpic.setStartTime(LocalDateTime.of(2025, 9, 26, 12, 0));
        otherEpic.setDuration(Duration.ofMinutes(10));

        assertNotEquals(epic, otherEpic);
    }
}