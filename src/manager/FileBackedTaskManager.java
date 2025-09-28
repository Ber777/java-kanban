package manager;

import model.*;
import exception.*;

import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.time.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        // Переработал конструктор с учетом комментария к спринту_7:
        super();
        this.file = file;
    }

    public static String toString(HistoryManager manager) {
        List<String> strList = manager.getHistory().stream().map(task ->
                String.valueOf(task.getId())).collect(Collectors.toList());
        return String.join(",", strList);
    }

    public void save() {
        try (Writer writer = new FileWriter(file, Charset.forName("windows-1251"))) {
            writer.write("id,type,name,status,description,epic,start_time,duration\n");
            HashMap<Long, String> allTasks;

            ArrayList<Task> tasks = super.getAllTasks();
            allTasks = tasks.stream().collect(Collectors.toMap(Task::getId, Task::toStringFromFile,
                    (a, b) -> b, HashMap::new));

            ArrayList<Epic> epics = super.getAllEpics();
            epics.forEach(epic -> allTasks.put(epic.getId(), epic.toStringFromFile()));

            ArrayList<SubTask> subtasks = super.getAllSubTasks();
            subtasks.forEach(subtask -> allTasks.put(subtask.getId(), subtask.toStringFromFile()));

            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value));
            }

            //writer.write("\n"); writer.write(toString(this.historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла.");
        }
    }

    public static Task fromString(String value, TaskType taskType) {
        ArrayList<Long> subTaskArrayList = new ArrayList<>();
        String epicId = null; LocalDateTime startDt = null;
        Duration duration = null; LocalDateTime endDt = null;

        String[] dataOfTask = value.split(",");
        int id = Integer.parseInt(dataOfTask[0]);
        String name = dataOfTask[2];
        Status status = Status.valueOf(dataOfTask[3]);
        String description = dataOfTask[4];
        if (!taskType.equals(TaskType.EPIC)) {
            epicId = dataOfTask[5].trim();
            if (!dataOfTask[6].equals("null") && !dataOfTask[7].equals("null")) {
                startDt = LocalDateTime.parse(dataOfTask[6].trim());
                duration = Duration.ofMinutes(Long.parseLong(dataOfTask[7].trim()));
            }
        } else {
            // Добавлена функциональность, чтобы исправить проблему "Не восстанавливается связь эпиков с подзадачами" спринт_7
                int idxSt = 0; int idxEnd = 0;
                for (int i = 0; i < dataOfTask.length; i++) {
                    if (dataOfTask[i].contains("[")) {
                        idxSt = i;
                    }
                    if (dataOfTask[i].contains("]")) {
                        idxEnd = i;
                    }
                }
                // Используем стрим
                subTaskArrayList = IntStream.rangeClosed(idxSt, idxEnd).mapToObj(i -> dataOfTask[i].trim())
                        .map(subId -> subId.replaceAll("\\[", ""))
                        .map(subId -> subId.replaceAll("]", ""))
                        .map(subId -> Long.parseLong(subId.trim())).collect(Collectors.toCollection(ArrayList::new));
                if (!dataOfTask[5 + (idxEnd - idxSt) + 1].equals("null")
                        && !dataOfTask[5 + (idxEnd - idxSt) + 2].equals("null")
                        && !dataOfTask[5 + (idxEnd - idxSt) + 3].equals("null")) {
                    startDt = LocalDateTime.parse(dataOfTask[5 + (idxEnd - idxSt) + 1].trim());
                    duration = Duration.ofMinutes(Integer.parseInt(dataOfTask[5 + (idxEnd - idxSt) + 2].trim()));
                    endDt = LocalDateTime.parse(dataOfTask[5 + (idxEnd - idxSt) + 3].trim());
                }
        }

        return switch (taskType) {
            case TASK -> new Task((long) id, name, description, status, startDt, duration);
            case EPIC -> new Epic(id, name, description, subTaskArrayList, startDt, duration, endDt);
            case SUBTASK ->
                    new SubTask((long) id, name, description, status, Long.valueOf(epicId), startDt, duration);
        };
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager(file);
        String data;
        try {
            data = Files.readString(Path.of(file.getAbsolutePath()), Charset.forName("windows-1251"));
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения файла.");
        }
        String[] lines = data.split("\n");
        boolean isTitle = true;
        boolean isTask = true;

        for (String line : lines) {
            if (isTitle) {
                isTitle = false;
                continue;
            }
            if (line.isEmpty() || line.equals("\r")) {
                isTask = false;
                continue;
            }
            if (isTask) {
                TaskType taskType = TaskType.valueOf(line.split(",")[1]);
                switch (taskType) {
                    case TASK:
                        Task task = fromString(line, TaskType.TASK);
                        fileBackedTasksManager.tasks.put(task.getId(), task);
                        break;
                    case SUBTASK:
                        SubTask subTask = (SubTask) fromString(line, TaskType.SUBTASK);
                        fileBackedTasksManager.subTasks.put(subTask.getId(), subTask);
                        break;
                    case EPIC:
                        Epic epic = (Epic) fromString(line, TaskType.EPIC);
                        fileBackedTasksManager.epics.put(epic.getId(), epic);
                        break;
                }
            }
        }
        return fileBackedTasksManager;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public Long add(Task task) {
        Long taskId = super.add(task);
        save();
        return taskId;
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public Task getTask(Long id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void deleteTask(Long id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Long add(Epic epic) {
        Long epicId = super.add(epic);
        save();
        return epicId;
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        save();
    }

    @Override
    public Epic getEpic(Long id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void deleteEpic(Long id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Long add(SubTask subTask) {
        Long subTaskId = super.add(subTask);
        save();
        return subTaskId;
    }

    @Override
    public void update(SubTask subTask) {
        super.update(subTask);
        save();
    }

    @Override
    public SubTask getSubTask(Long id) {
        SubTask subtask = super.getSubTask(id);
        save();
        return subtask;
    }

    @Override
    public void deleteSubTask(Long id) {
        super.deleteSubTask(id);
        save();
    }
}
