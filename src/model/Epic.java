package model;
import java.util.ArrayList;

public class Epic extends Task{
    //private long id;  // излишнее, поле наследуем от Task.
    private ArrayList<Long> subTaskArrayList;  // Исправил, храним теперь только id подзадач

    public Epic(String name, String description){
        super(0L, name, description);
        subTaskArrayList = new ArrayList<>();
    }

    public Epic(Long id, String name, String description) {
        super(id, name, description);
        subTaskArrayList = new ArrayList<>();
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
