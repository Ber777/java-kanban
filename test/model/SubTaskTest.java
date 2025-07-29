package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubTaskTest {
    // Тест3: экземпляры класса SubTask (наследник класса Task) равны друг другу, если равен их id:
    @Test
    public void shouldReturnEqualsSubTasks() {
        Epic epic = new Epic("Эпик 1", "Мой первый эпик");
        SubTask subTask1 = new SubTask(1L, "Первый сабтаск первого эпика", "", Status.NEW, epic.getId());
        SubTask subTask2 = new SubTask(1L, "Второй сабтаск первого эпика", "", Status.IN_PROGRESS, epic.getId());
        Assertions.assertEquals(subTask1, subTask2, "Ошибка! Наследники класса Task должны быть равны друг другу, если равен их id;");
    }
}