import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MessageService {

    private final Map<Socket, String> sockets = new HashMap<>();

    public void open(Socket socket, String username) {
        sockets.put(socket, username);
    }

    public void exit(Socket socket) {
        sockets.remove(socket);
    }

    public void publ(Socket socket, String message) {
        // TODO
    }

    public void admn(Socket socket, String message) {
        // TODO
    }

    public void show(String username, String message) {
        // TODO
    }
}
