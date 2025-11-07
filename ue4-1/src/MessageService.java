import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MessageService {

    private final Set<String> users = new HashSet<>();
    private final List<Protocol.Msg> messages = new ArrayList<>();

    public void addUser(String user) throws IllegalArgumentException {
        if (!users.add(user)) {
            throw new IllegalArgumentException("User already exists");
        }
    }

    public void send(Protocol.Msg msg) throws IllegalArgumentException {
        if (!users.contains(msg.from()) || !users.contains(msg.to())) {
            throw new IllegalArgumentException();
        }
        messages.add(msg);
    }

    public List<Protocol.Msg> get(String to) throws IllegalArgumentException {
        if (!users.contains(to)) {
            throw new IllegalArgumentException();
        }

        return messages
            .stream()
            .filter(msg -> msg.to().equals(to))
            .toList();
    }
}
