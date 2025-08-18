package manager;
import model.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    // новый метод для удаления задачи из просмотра
    void remove(int id);

    List<Task> getHistory();

}
