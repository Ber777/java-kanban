import java.util.ArrayList;

public class Epic extends Task{
    private long id;
    ArrayList<SubTask> subTaskArrayList;

    Epic(String name, String description){
        super(0L, name, description);
        subTaskArrayList = new ArrayList<>();
    }

    public Epic(Long id, String name, String description) {
        super(id, name, description);
        subTaskArrayList = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTask() {
        return subTaskArrayList;
    }

    public void setSubTask(ArrayList<SubTask> subTaskArrayList) {
        this.subTaskArrayList = subTaskArrayList;
    }

    public void clearSubTaskList() {
        this.subTaskArrayList.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subTaskArrayList=" + subTaskArrayList.toString() +
                '}';
    }
}
