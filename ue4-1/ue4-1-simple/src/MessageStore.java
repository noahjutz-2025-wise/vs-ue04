import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MessageStore {
  private final AtomicReference<List<String>> users = new AtomicReference<>(new ArrayList<>());
  private final AtomicReference<List<Message>> messages = new AtomicReference<>(new ArrayList<>());

  public void addUser(String user) {
    users.updateAndGet(list -> {
      list.add(user);
      return list;
    });
  }

  public void addMessage(String from, String to, String body) {
    messages.updateAndGet(list -> {
      list.add(new Message(from, to, body));
      return list;
    });
  }

  public List<Message> getMessagesTo(String to) {
    return messages.get().stream().filter(msg -> msg.to().equals(to)).toList();
  }
}
