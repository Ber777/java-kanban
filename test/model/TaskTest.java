package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    // Тест1: экземпляры класса Task равны друг другу, если равен их id:
    @Test
    public void shouldReturnEqualsTasks() {
        Task task1 = new Task(1L, "Задача 1", "Первый обычный таск", Status.NEW);
        Task task2 = new Task(1L, "Задача 1", "Первый обычный таск", Status.DONE);
        Assertions.assertEquals(task1, task2, "Ошибка! Экзепляры класса Task должны быть равны друг другу, если равен их id;");
    }

}