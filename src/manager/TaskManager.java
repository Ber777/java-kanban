package manager;
import model.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // 2a: Получение списка всех задач:
    ArrayList<Task> getAllTasks();

    // Печать всех задач:
    void printAllTasks();

    // 2a: Получение списка всех подзадач:
    ArrayList<SubTask> getAllSubTasks();

    // Печать всех подзадач:
    void printAllSubTasks();

    // 2a: Получение списка всех эпиков:
    ArrayList<Epic> getAllEpics();

    // Печать всех эпиков:
    void printAllEpics();

    // 2b: Удаление всех задач:
    void deleteAllTasks();

    // 2b: Удаление всех подзадач:
    void deleteAllSubTasks();

    // 2b: Удаление всех эпиков:
    void deleteAllEpics();

    // 2c: Возвращает задачу по идентификатору:
    Task getTask(Long id);

    // 2c: Возвращает подзадачу по идентификатору:
    SubTask getSubTask(Long id);

    // 2c: Возвращает эпик по идентификатору:
    Epic getEpic(Long id);

    // 2d: Создание задачи:
    Long add(Task task);

    // 2d: Создание подзадачи (влечет за собой добавление в определенный эпик):
    Long add(SubTask subTask);

    // 2d: Создание эпика
    Long add(Epic epic);

    // 2e: Обновление задач:
    void update(Task task);

    // 2e: Обновление подзадачи:
    void update(SubTask subTask);

    // 2e: Обновление эпика:
    void update(Epic epic);

    // 2f: Удаление по идентификатору:
    void deleteTask(Long id);

    // 2f: Удаление подзадачи по идентификатору:
    void deleteSubTask(Long id);

    // 2f: Удаление эпика по идентификатору:
    void deleteEpic(Long id);

    // 3a: Получение списка подзадач эпика:
    ArrayList<Long> getAllSubTasksOfEpic(Long id);

    // Печать подзадач определенного эпика:
    void printAllSubTasksOfEpic(Long id);

    // История просмотров задач:
    List<Task> getHistory();
}
