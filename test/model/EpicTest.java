package model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    // Тест2: экземпляры класса Epic (наследник класса Task) равны друг другу, если равен их id:
    @Test
    public void shouldReturnEqualsEpics() {
        Epic epic1 = new Epic(1L, "Эпик 1", "Мой первый эпик");
        Epic epic2 = new Epic(1L, "Эпик 2", "Мой второй эпик");
        Assertions.assertEquals(epic1, epic2,"Ошибка! Наследники класса Task должны быть равны друг другу, если равен их id;");
    }
}