package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private long id;
    private String name;
    private String description;
    private Status status;
    protected TaskType taskType;
    protected Duration duration;
    protected LocalDateTime startTime;

    static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Task(Long id, String name, String description, Status status, LocalDateTime time, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.startTime = time;
        this.duration = duration;
    }

    public Task(String name, String description, Status status, LocalDateTime time, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.startTime = time;
        this.duration = duration;
    }

    public Task(Long id, String name, String description, LocalDateTime time, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.taskType = TaskType.TASK;
        this.startTime = time;
        this.duration = duration;
    }

    public Task(String name, String description, LocalDateTime time, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.taskType = TaskType.TASK;
        this.startTime = time;
        this.duration = duration;
    }

    public Task(Long id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
    }

    public Task(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.taskType = TaskType.TASK;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.taskType = TaskType.TASK;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null)
            return null;
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", startTime=" + (startTime == null ? "null" : startTime) + '\'' +
                ", duration=" + (duration == null ? "null" : duration.toMinutes()) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task task = (Task) obj;
        return this.id == task.id;
    }

    @Override
    public int hashCode() {
        int hash = 11;
        if (this.name != null) {
            hash = hash + this.name.hashCode();
        }
        hash = hash * 31;
        if (this.description != null) {
            hash = hash + this.description.hashCode();
        }
        return hash;
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", id, taskType, name, status, description, "",
                startTime, (duration == null ? "null" : duration.toMinutes()));
    }
}
