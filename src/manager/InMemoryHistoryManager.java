package manager;
import model.Task;
import model.Node;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    //private static final int MAX_LIST_SIZE = 10;  // Задаем константу для списка просмотра
    //private static final List<Task> historyList = new ArrayList<>(MAX_LIST_SIZE);;  // Сам список для просмотра задач
    private final Map<Integer, Node<Task>> savedTasksMap;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        savedTasksMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        // Добавил проверку объекта на null
        if (task != null) {
            linkLast(task);
            /*if (historyList.size() == MAX_LIST_SIZE) {
                historyList.removeFirst();
            }
            historyList.add(task);*/
        }
    }

    // Реализация нового метода интерфейса HistoryManager:
    @Override
    public void remove(int id) {
        removeNode(savedTasksMap.get(id));
    }

    //История просмотров задач:
    @Override
    public List<Task> getHistory() {
        // Возвращаем копию истории, чтобы сторонние классы не смогли вносить свои изменения.
        //return historyList;
        //return new ArrayList<>(historyList);
        return getTasks();
    }

    private void linkLast(Task task) {
        if (savedTasksMap.containsKey((int) task.getId())) {
            removeNode(savedTasksMap.get((int) task.getId()));
        }
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(task, tail, null);
        tail = newNode;
        savedTasksMap.put((int) task.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.getNext();
            final Node<Task> prev = node.getPrev();
            node.setData(null);

            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node) {
                head = next;
                head.setPrev(null);
            } else if (tail == node) {
                tail = prev;
                tail.setNext(null);
            } else {
                prev.setNext(next);
                next.setPrev(prev);
            }
        }
    }
}
