package model;

public class SubTask extends Task{
    private long epicId;

    public SubTask(String name, String description, Status status, Long epicId) {
        super(0L, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(Long idTask, String name, String description, Status status, Long epicId) {
        super(idTask, name, description, status);
        this.epicId = epicId;
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
                ", epicId=" + this.epicId +
                '}';
    }
}
