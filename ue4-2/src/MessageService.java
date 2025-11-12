import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MessageService {

    private final Map<Socket, String> sockets = new HashMap<>();

    public void open(Socket socket, String username) {
        sockets.put(socket, username);
    }
}
