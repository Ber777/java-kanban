package exception;

public class TimeException extends RuntimeException {
    public TimeException(String name, Long taskId) {
        super("Задача '" + name + "' пересекается по времени с задачей id=:" + taskId);
    }
}