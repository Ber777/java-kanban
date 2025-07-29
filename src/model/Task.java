package model;

public class Task {
    private long id;
    private String name;
    private String description;
    private Status status;

    public Task(Long id, String name, String description, Status status){
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Long id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;  // По умолчанию
    }

    public Task(String name, String description){
        this.name = name;
        this.description = description;
        this.status = Status.NEW;  // По умолчанию
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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    // Добавил методы, чтобы задачи с одинаковыми Id были равны (выявил проблему тестами 1-3)
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
}
