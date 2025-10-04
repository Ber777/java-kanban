package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private long epicId;

    public SubTask(String name, String description, Status status, Long epicId) {
        super(0L, name, description, status);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(Long idTask, String name, String description, Status status, Long epicId) {
        super(idTask, name, description, status);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(String name, String description, Status status, Long epicId, LocalDateTime time, Duration duration) {
        super(name, description, status, time, duration);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(Long idTask, String name, String description, Status status, Long epicId, LocalDateTime time, Duration duration) {
        super(idTask, name, description, status, time, duration);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public void setEpicIdSub(Long epicId) {
        this.epicId = epicId;
    }

    public long getEpicIdSub() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + this.epicId + '\'' +
                ", startTime=" + (startTime == null ? "null" : startTime) + '\'' +
                ", duration=" + (duration == null ? "null" : duration.toMinutes()) +
                '}';
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", getId(), getTaskType(), getName(), getStatus(),
                getDescription(), this.epicId, getStartTime(), (getDuration() == null ? "null" : getDuration().toMinutes()));
    }
}
