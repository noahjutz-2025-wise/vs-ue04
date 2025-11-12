import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MessageService {

    private final Map<Socket, String> sockets = new HashMap<>();

    public void open(Socket socket, String username) {
        // TODO ADMN
        sockets.put(socket, username);
    }

    public void exit(Socket socket) {
        // TODO ADMN
        sockets.remove(socket);
    }

    public void publ(Socket socket, String message) {
        // TODO SHOW
    }
}
