package manager;
import model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Long, Task> tasks;
    private final HashMap<Long, SubTask> subTasks;
    private final HashMap<Long, Epic> epics;
    private Long id;
    private final InMemoryHistoryManager historyManager;  // Добавили для истории просмотра

    public InMemoryTaskManager(){
        this.id = 1L;
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager();  // инициализация
    }

    private Long getUniqueId(){return this.id++;}

    // 2a: Получение списка всех задач:
    @Override
    public ArrayList<Task> getAllTasks(){return new ArrayList<>(tasks.values());}

    // Печать всех задач:
    @Override
    public void printAllTasks(){
        for (Long idKey: tasks.keySet()) {
            Task task = tasks.get(idKey);
            System.out.println(task);
        }
        if(tasks.isEmpty()) {
            System.out.println("Задачи отсутствуют.");
        }
    }

    // 2a: Получение списка всех подзадач:
    @Override
    public ArrayList<SubTask> getAllSubTasks(){return new ArrayList<>(subTasks.values());}

    // Печать всех подзадач:
    @Override
    public void printAllSubTasks(){
        if(subTasks.isEmpty()) {
            System.out.println("Подзадачи отсутствуют.");
        } else {
            System.out.println(new ArrayList<>(subTasks.values()));
        }
    }

    // 2a: Получение списка всех эпиков:
    @Override
    public ArrayList<Epic> getAllEpics(){return new ArrayList<>(epics.values());}

    // Печать всех эпиков:
    @Override
    public void printAllEpics(){
        for (Long idKey: epics.keySet()) {
            Epic epic = epics.get(idKey);
            System.out.println(epic);
        }
        if(epics.isEmpty()) {
            System.out.println("Эпики отсутствуют.");
        }
    }

    // 2b: Удаление всех задач:
    @Override
    public void deleteAllTasks(){
        tasks.clear();
        System.out.println("Все задачи удалены.");
    }

    // 2b: Удаление всех подзадач:
    @Override
    public void deleteAllSubTasks(){
        subTasks.clear();
        for(Long key : epics.keySet()){
            Epic epic = epics.get(key);
            epic.clearSubTaskList();
            updateEpicStatus(epic.getId());
        }
        System.out.println("Все подзадачи удалены.");
    }

    // 2b: Удаление всех эпиков:
    @Override
    public void deleteAllEpics(){
        subTasks.clear();
        epics.clear();
        System.out.println("Все эпики и их подзадачи удалены.");
    }

    // 2c: Возвращает задачу по идентификатору:
    @Override
    public Task getTask(Long id){
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            System.out.println("Задача №" + id + ':' + task);
            historyManager.add(tasks.get(id));  // Добавляем в список просмотренных
            return task;
        } else {
            System.out.println("Такой задачи не найдено.");
        }
        return null;
    }

    // 2c: Возвращает подзадачу по идентификатору:
    @Override
    public SubTask getSubTask(Long id){
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            System.out.println("Подзадача №" + id + ':' + subTask);
            historyManager.add(subTasks.get(id));  // Добавляем в список просмотренных
            return subTask;
        } else {
            System.out.println("Такой подзадачи не найдено.");
        }
        return null;
    }

    // 2c: Возвращает эпик по идентификатору:
    @Override
    public Epic getEpic(Long id){
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            System.out.println("Эпик №" + id + ':' + epic);
            historyManager.add(epics.get(id));  // Добавляем в список просмотренных
            return epic;
        } else {
            System.out.println("Такого эпика не найдено.");
        }
        return null;
    }

    // 2d: Создание задачи:
    @Override
    public Long add(Task task){
        task.setId(getUniqueId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    // 2d: Создание подзадачи (влечет за собой добавление в определенный эпик):
    @Override
    public Long add(SubTask subTask){
        Long epicNum = subTask.getEpicIdSub();
        if (epics.containsKey(epicNum)){
            subTask.setId(getUniqueId());
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicIdSub());
            updateEpicBySubTasks(epic, subTask);
            updateEpicStatus(epic.getId());
            return subTask.getId();
        } else {
            System.out.println("Невозможно добавить подзадачу. Укажите существующий эпик.");
        }
        return -1L;
    }

    // 2d: Создание эпика
    @Override
    public Long add(Epic epic){
        epic.setId(getUniqueId());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    // Запись подзадач в эпик:
    public void updateEpicBySubTasks(Epic epic, SubTask subTask) {
        ArrayList<Long> subTasks = new ArrayList<>(epic.getSubTask());
        subTasks.add(subTask.getId());  // Кладем только id
        epic.setSubTask(subTasks);
    }

    // 2e: Обновление задач:
    @Override
    public void update(Task task){
        if(tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            if (oldTask.getId() == task.getId()){
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());
                System.out.println("Задача №" + task.getId() + " обновлена.");
            }
        } else {
            System.out.println("Такой задачи не найдено. Задача не обновлена.");
        }
    }

    // 2e: Обновление подзадачи:
    @Override
    public void update(SubTask subTask){
        if(subTasks.containsKey(subTask.getId()) && epics.containsKey(subTask.getEpicIdSub())){  // Принято, проверки объеденены
                SubTask oldSubTask = subTasks.get(subTask.getId());
                if (oldSubTask.getId() == subTask.getId()){
                    oldSubTask.setName(subTask.getName());
                    oldSubTask.setDescription(subTask.getDescription());
                    oldSubTask.setStatus(subTask.getStatus());
                    oldSubTask.setEpicIdSub(subTask.getEpicIdSub());
                }
                updateEpicStatus(subTask.getEpicIdSub());
                System.out.println("Подзадача №" + subTask.getId() + " обновлена. Статус эпика №" + subTask.getEpicIdSub() + " мог поменяться.");
        } else {
            System.out.println("Такой подзадачи не найдено или указан не существующий эпик. Подзадача не обновлена.");
        }
    }

    // 2e: Обновление эпика:
    @Override
    public void update(Epic epic){
        if(epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            if (oldEpic.getId() == epic.getId()) {
                oldEpic.setName(epic.getName());
                oldEpic.setDescription(epic.getDescription());
            }
            updateEpicStatus(epic.getId());
            System.out.println("Эпик №" + epic.getId() + " обновлен.");
        } else {
            System.out.println("Такого эпика не найдено. Эпик не обновлен.");
        }
    }

    // 2f: Удаление по идентификатору:
    @Override
    public void deleteTask(Long id){
        if (tasks.containsKey(id)) {
            tasks.remove(id);
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
            ArrayList<Long> sub1 = epics.get(epicId).getSubTask();
            sub1.remove(id);
            subTasks.remove(id);
            updateEpicStatus(epicId);
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
            for (Long idKey : epic.getSubTask()) {
                subTasks.remove(idKey);
            }
            epic.clearSubTaskList();
            epics.remove(id);
            System.out.println("Эпик №" + id + " удален. Все его подзадачи были удалены.");
        } else {
            System.out.println("Идентификатор " + id + " отсутствует в системе. Эпик не был удален.");
        }
    }

    // 3a: Получение списка подзадач эпика:
    @Override
    public ArrayList<Long> getAllSubTasksOfEpic(Long id){
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
    public void printAllSubTasksOfEpic(Long id){
        if (epics.containsKey(id)) {
            ArrayList<Long> subTaskList;
            subTaskList = epics.get(id).getSubTask();
            System.out.println("Подзадачи эпика №" + id + ':');
            if (subTaskList.isEmpty()) {
                System.out.println("У эпика №" + id + " подзадач нет.");
            } else {
                System.out.println(subTaskList);
            }
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
}
