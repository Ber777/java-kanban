package manager;
import model.Task;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_LIST_SIZE = 10;  // Задаем константу для списка просмотра
    private static final List<Task> historyList = new ArrayList<>(MAX_LIST_SIZE);;  // Сам список для просмотра задач

    @Override
    public void add(Task task) {
        if (historyList.size() == MAX_LIST_SIZE) {
            historyList.removeFirst();
        }
        historyList.add(task);
    }

    //История просмотров задач:
    @Override
    public List<Task> getHistory() {
        return historyList;
    }

}
