package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Long> subTaskArrayList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(0L, name, description);
        this.taskType = TaskType.EPIC;
        subTaskArrayList = new ArrayList<>();
    }

    public Epic(Long id, String name, String description) {
        super(id, name, description);
        this.taskType = TaskType.EPIC;
        subTaskArrayList = new ArrayList<>();
    }

    // Добавил конструктор, чтобы исправить проблему "Не восстанавливается связь эпиков с подзадачами" по спринту_7
    public Epic(long id, String name, String description, ArrayList<Long> subTaskArrayList) {
        super(id, name, description);
        this.taskType = TaskType.EPIC;
        this.subTaskArrayList = subTaskArrayList;
    }

    public Epic(long id, String name, String description, ArrayList<Long> subTaskArrayList
            , LocalDateTime time, Duration duration, LocalDateTime endTime) {
        super(id, name, description, time, duration);
        this.taskType = TaskType.EPIC;
        this.subTaskArrayList = subTaskArrayList;
        this.endTime = endTime;
    }

    public Epic(String name, String description, LocalDateTime time, Duration duration) {
        super(name, description, time, duration);
        this.taskType = TaskType.EPIC;
        this.subTaskArrayList = new ArrayList<>();
    }

    public ArrayList<Long> getSubTask() {
        return subTaskArrayList;
    }

    public void setSubTask(ArrayList<Long> subTaskArrayList) {
        this.subTaskArrayList = subTaskArrayList;
    }

    public void clearSubTaskList() {
        this.subTaskArrayList.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // Переопределил метод, чтобы исправить проблему "Не восстанавливается связь эпиков с подзадачами" по спринту_7
    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", getId(), getTaskType(), getName(), getStatus(),
                getDescription(), this.subTaskArrayList, getStartTime()
                , (getDuration() == null ? "null" : getDuration().toMinutes()), this.endTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subTaskArrayList=" + subTaskArrayList.toString() +
                ", startTime=" + (startTime == null ? "null" : startTime) + '\'' +
                ", duration=" + (duration == null ? "null" : duration.toMinutes()) + '\'' +
                ", endTime=" + (endTime == null ? "null" : endTime) +
                '}';
    }
}
