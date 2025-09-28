package manager;

import model.*;
import exception.*;

import java.util.*;
import java.time.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Long, Task> tasks;
    protected final HashMap<Long, SubTask> subTasks;
    protected final HashMap<Long, Epic> epics;
    protected Long id;
    protected final InMemoryHistoryManager historyManager;
    protected Set<Task> prioritizedTasks;  // Спринт_8

    public InMemoryTaskManager() {
        this.id = 1L;
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    private Long getUniqueId() {
        return this.id++;
    }

    // 2a: Получение списка всех задач:
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Печать всех задач:
    @Override
    public void printAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Задачи отсутствуют.");
        } else {
            tasks.keySet().stream().map(tasks::get).forEach(System.out::println);
        }
    }

    @Override
    public void printPrioritization() {
        if (prioritizedTasks.isEmpty()) {
            System.out.println("Приоритизированный список задач отсутствует.");
        } else {
            prioritizedTasks.forEach(System.out::println);
        }
    }

    // 2a: Получение списка всех подзадач:
    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // Печать всех подзадач:
    @Override
    public void printAllSubTasks() {
        if (subTasks.isEmpty()) {
            System.out.println("Подзадачи отсутствуют.");
        } else {
            System.out.println(new ArrayList<>(subTasks.values()));
        }
    }

    // 2a: Получение списка всех эпиков:
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Печать всех эпиков:
    @Override
    public void printAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Эпики отсутствуют.");
        } else {
            epics.keySet().stream().map(epics::get).forEach(System.out::println);
        }
    }

    // 2b: Удаление всех задач:
    @Override
    public void deleteAllTasks() {
        prioritizedTasks.removeIf(task -> task.getClass().equals(Task.class));
        tasks.keySet().forEach(id1 -> historyManager.remove(Math.toIntExact(id1)));
        tasks.clear();
        System.out.println("Все задачи удалены.");
    }

    // 2b: Удаление всех подзадач:
    @Override
    public void deleteAllSubTasks() {
        prioritizedTasks.removeIf(task -> task.getClass().equals(SubTask.class));
        subTasks.keySet().forEach(id1 -> historyManager.remove(Math.toIntExact(id1)));
        subTasks.clear();
        epics.keySet().stream().map(epics::get).forEach(epic -> {
            epic.clearSubTaskList();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic);
        });
        System.out.println("Все подзадачи удалены.");
    }

    // 2b: Удаление всех эпиков:
    @Override
    public void deleteAllEpics() {
        prioritizedTasks.removeIf(task -> task.getClass().equals(SubTask.class));
        epics.keySet().forEach(id1 -> historyManager.remove(Math.toIntExact(id1)));
        subTasks.keySet().forEach(id1 -> historyManager.remove(Math.toIntExact(id1)));
        subTasks.clear();
        epics.clear();
        System.out.println("Все эпики и их подзадачи удалены.");
    }

    // 2c: Возвращает задачу по идентификатору:
    @Override
    public Task getTask(Long id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            System.out.println("Задача №" + id + ':' + task);
            historyManager.add(tasks.get(id));
            return task;
        } else {
            System.out.println("Такой задачи не найдено.");
        }
        return null;
    }

    // 2c: Возвращает подзадачу по идентификатору:
    @Override
    public SubTask getSubTask(Long id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            System.out.println("Подзадача №" + id + ':' + subTask);
            historyManager.add(subTasks.get(id));
            return subTask;
        } else {
            System.out.println("Такой подзадачи не найдено.");
        }
        return null;
    }

    // 2c: Возвращает эпик по идентификатору:
    @Override
    public Epic getEpic(Long id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            System.out.println("Эпик №" + id + ':' + epic);
            historyManager.add(epics.get(id));
            return epic;
        } else {
            System.out.println("Такого эпика не найдено.");
        }
        return null;
    }

    // 2d: Создание задачи:
    @Override
    public Long add(Task task) {
        checkTaskTime(task);
        task.setId(getUniqueId());
        tasks.put(task.getId(), task);
        addTaskInPrioritizedList(task);
        return task.getId();
    }

    // 2d: Создание подзадачи:
    @Override
    public Long add(SubTask subTask) {
        Long epicNum = subTask.getEpicIdSub();
        if (epics.containsKey(epicNum)) {
            checkTaskTime(subTask);
            subTask.setId(getUniqueId());
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicIdSub());
            updateEpicBySubTasks(epic, subTask);
            updateEpicStatus(epic.getId());
            updateEpicTime(epic);
            addTaskInPrioritizedList(subTask);
            return subTask.getId();
        } else {
            System.out.println("Невозможно добавить подзадачу. Укажите существующий эпик.");
        }
        return -1L;
    }

    // 2d: Создание эпика
    @Override
    public Long add(Epic epic) {
        epic.setId(getUniqueId());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    // Запись подзадач в эпик:
    public void updateEpicBySubTasks(Epic epic, SubTask subTask) {
        ArrayList<Long> subTasks = new ArrayList<>(epic.getSubTask());
        subTasks.add(subTask.getId());
        epic.setSubTask(subTasks);
    }

    // 2e: Обновление задач:
    @Override
    public void update(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            if (oldTask.getId() == task.getId()) {
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());
                oldTask.setStartTime(task.getStartTime());
                oldTask.setDuration(task.getDuration());
                checkTaskTime(task);
                System.out.println("Задача №" + task.getId() + " обновлена.");
                addTaskInPrioritizedList(task);
            }
        } else {
            System.out.println("Такой задачи не найдено. Задача не обновлена.");
        }
    }

    // 2e: Обновление подзадачи:
    @Override
    public void update(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId()) && epics.containsKey(subTask.getEpicIdSub())) {
            SubTask oldSubTask = subTasks.get(subTask.getId());
            if (oldSubTask.getId() == subTask.getId()) {
                oldSubTask.setName(subTask.getName());
                oldSubTask.setDescription(subTask.getDescription());
                oldSubTask.setStatus(subTask.getStatus());
                oldSubTask.setEpicIdSub(subTask.getEpicIdSub());
                oldSubTask.setStartTime(subTask.getStartTime());
                oldSubTask.setDuration(subTask.getDuration());
                checkTaskTime(subTask);
                addTaskInPrioritizedList(subTask);
            }
            updateEpicStatus(subTask.getEpicIdSub());
            Epic epic = epics.get(subTask.getEpicIdSub());
            updateEpicTime(epic);
            System.out.println("Подзадача №" + subTask.getId() + " обновлена. Статус эпика №"
                    + subTask.getEpicIdSub() + " мог поменяться.");
        } else {
            System.out.println("Такой подзадачи не найдено или указан не существующий эпик. Подзадача не обновлена.");
        }
    }

    // 2e: Обновление эпика:
    @Override
    public void update(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            if (oldEpic.getId() == epic.getId()) {
                oldEpic.setName(epic.getName());
                oldEpic.setDescription(epic.getDescription());
            }
            updateEpicStatus(epic.getId());
            updateEpicTime(epic);
            System.out.println("Эпик №" + epic.getId() + " обновлен.");
        } else {
            System.out.println("Такого эпика не найдено. Эпик не обновлен.");
        }
    }

    // 2f: Удаление по идентификатору:
    @Override
    public void deleteTask(Long id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            prioritizedTasks.remove(tasks.get(id));
            historyManager.remove(Math.toIntExact(id));
            System.out.println("Задача №" + id + " удалена.");
        } else {
            System.out.println("Идентификатор " + id + " отсутствует в системе. Задача не была удалена.");
        }
    }

    // 2f: Удаление подзадачи по идентификатору:
    @Override
    public void deleteSubTask(Long id) {
        if (subTasks.containsKey(id)) {
            Long epicId = subTasks.get(id).getEpicIdSub();
            SubTask subTask = subTasks.get(id);
            ArrayList<Long> sub1 = epics.get(epicId).getSubTask();
            sub1.remove(id);
            subTasks.remove(id);
            historyManager.remove(Math.toIntExact(id));
            prioritizedTasks.remove(subTask);
            updateEpicStatus(epicId);
            Epic epic = epics.get(subTask.getEpicIdSub());
            updateEpicTime(epic);
            System.out.println("Подзадача №" + id + " удалена. Статус эпика №" + epicId + " мог поменяться.");
        } else {
            System.out.println("Идентификатор " + id + " отсутствует в системе. Подзадача не была удалена.");
        }
    }

    // 2f: Удаление эпика по идентификатору:
    @Override
    public void deleteEpic(Long id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epic.getSubTask().forEach(idKey -> {
                subTasks.remove(idKey);
                historyManager.remove(Math.toIntExact(idKey));
            });
            epic.clearSubTaskList();
            epics.remove(id);
            historyManager.remove(Math.toIntExact(id));
            epic.getSubTask().forEach(id1 -> historyManager.remove(Math.toIntExact(id1)));
            prioritizedTasks.removeIf(task -> epic.getSubTask().contains(task.getId()));
            subTasks.values().removeIf(c -> c.getEpicIdSub() == id);
            System.out.println("Эпик №" + id + " удален. Все его подзадачи были удалены.");
        } else {
            System.out.println("Идентификатор " + id + " отсутствует в системе. Эпик не был удален.");
        }
    }

    // 3a: Получение списка подзадач эпика:
    @Override
    public ArrayList<Long> getAllSubTasksOfEpic(Long id) {
        if (epics.containsKey(id)) {
            ArrayList<Long> subTasksList;
            subTasksList = epics.get(id).getSubTask();
            return subTasksList;
        } else {
            System.out.println("Такого эпика не найдено.");
        }
        return null;
    }

    // Печать подзадач определенного эпика:
    @Override
    public void printAllSubTasksOfEpic(Long id) {
        if (epics.containsKey(id)) {
            ArrayList<Long> subTaskList;
            subTaskList = epics.get(id).getSubTask();
            System.out.println("Подзадачи эпика №" + id + ':');
            if (subTaskList.isEmpty()) {
                System.out.println("У эпика №" + id + " подзадач нет.");
            } else System.out.println(subTaskList);
        } else {
            System.out.println("Такого эпика не найдено.");
        }
    }

    // 4b: Обновление статуса эпика:
    private void updateEpicStatus(Long epicId) {
        Long cntNew = 0L;
        Long cntDone = 0L;
        Long cntSubTasks = (long) epics.get(epicId).getSubTask().size();

        for (Long idKey : epics.get(epicId).getSubTask()) {
            if (subTasks.get(idKey).getStatus() == Status.NEW) {
                cntNew++;
            } else if (subTasks.get(idKey).getStatus() == Status.DONE) {
                cntDone++;
            }
        }

        Epic epic = epics.get(epicId);
        if (cntNew.equals(cntSubTasks) || (cntSubTasks == 0)) {
            epic.setStatus(Status.NEW);
        } else if (cntDone.equals(cntSubTasks)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    // История просмотров задач:
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    protected void addTaskInPrioritizedList(Task task) {
        if ((task.getStartTime() != null) || (task instanceof Epic)) {
            prioritizedTasks.add(task);
        }
    }

    protected static Comparator<Task> comparatorByTime = (task1, task2) -> {
        LocalDateTime endTime1 = task1.getEndTime();
        if (endTime1.isBefore(task2.getStartTime()))
            return -1;
        if (task1.getStartTime().isAfter(task2.getEndTime()))
            return 1;
        return 0;
    };

    protected void updateEpicTime(Epic epic) {
        if (epic.getSubTask().isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            return;
        }

        Optional<LocalDateTime> startTime = epic.getSubTask().stream()
                .map(this::getSubTask)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> endTime = epic.getSubTask().stream()
                .map(this::getSubTask)
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        Duration duration = epic.getSubTask().stream()
                .map(this::getSubTask)
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(startTime.orElse(null));
        epic.setEndTime(endTime.orElse(null));
        epic.setDuration(duration);
    }

    private void checkTaskTime(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null)
            return;

        if (prioritizedTasks.isEmpty())
            return;

        int left = 0;
        int right = prioritizedTasks.size() - 1;
        List<Task> list = prioritizedTasks.stream().toList();

        while (left <= right) {
            int index = left;
            Task otherTask = list.get(index);
            int result = comparatorByTime.compare(task, otherTask);
            if (result == 0)
                throw new TimeException(task.getName(), otherTask.getId());

            if (result < 0) {
                right = index - 1;
            } else {
                left = index + 1;
            }
        };
    }
}
