import java.io.Serializable;
import java.time.LocalDateTime;
//copy and pasted from hw5 with move added so that we can have client send updated
//to game state and update from there
public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    public enum Type {
        REGISTER,
        DIRECT,
        SERVER,
        ERROR,
        MOVE
    }
    private Type type;
    private String sender;
    private String recipient;
    private String message;
    private LocalDateTime timeSent;
    private Move move;
    public Message(Type type, String sender, String recipient, String message, Move move) {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.timeSent = LocalDateTime.now();
        this.move = move;
    }
    public Message(Type type, String sender, String message, Move move) {
        this.type = type;
        this.sender = sender;
        this.message = message;
        this.timeSent = LocalDateTime.now();
        this.move = move;
    }
    public Type getType() { return type; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public LocalDateTime getTimeSent() { return timeSent; }
    public Move getMove() { return move; }
}
