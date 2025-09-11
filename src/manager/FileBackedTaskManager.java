package manager;

import model.*;
import exception.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        String fileName = "data.csv";
        this.file = new File(fileName);
        if (!file.isFile()) {
            try {
                Files.createFile(Paths.get(fileName));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла.");
            }
        }
    }

    public static String toString(HistoryManager manager) {
        List<String> strList = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            strList.add(String.valueOf(task.getId()));
        }
        return String.join(",", strList);
    }

    public void save() {
        try (Writer writer = new FileWriter(file, Charset.forName("windows-1251"))) {
            writer.write("id,type,name,status,description,epic\n");
            HashMap<Long, String> allTasks = new HashMap<>();

            ArrayList<Task> tasks = super.getAllTasks();
            for (Task task : tasks) {
                allTasks.put(task.getId(), task.toStringFromFile());
            }

            ArrayList<Epic> epics = super.getAllEpics();
            for (Epic epic : epics) {
                allTasks.put(epic.getId(), epic.toStringFromFile());
            }

            ArrayList<SubTask> subtasks = super.getAllSubTasks();
            for (SubTask subtask : subtasks) {
                allTasks.put(subtask.getId(), subtask.toStringFromFile());
            }

            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value));
            }
            writer.write("\n");
            writer.write(toString(this.historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла.");
        }
    }

    public static Task fromString(String value, TaskType taskType) {
        String[] dataOfTask = value.split(",", 6);
        int id = Integer.parseInt(dataOfTask[0]);
        String name = dataOfTask[2];
        Status status = Status.valueOf(dataOfTask[3]);
        String description = dataOfTask[4];
        String epicId = dataOfTask[5].trim();

        return switch (taskType) {
            case TASK -> new Task((long) id, name, description, status);
            case EPIC -> new Epic((long) id, name, description);
            case SUBTASK ->
                    new SubTask((long) id, name, description, status, Long.valueOf(epicId));
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
