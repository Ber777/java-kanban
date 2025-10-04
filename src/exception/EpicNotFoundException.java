package exception;

public class EpicNotFoundException extends RuntimeException {
    public EpicNotFoundException(long epicId) {
        super("Эпик id:" + epicId + " не существует");
    }
}
