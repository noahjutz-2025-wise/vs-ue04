import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageService {

    private final Map<Socket, String> sockets;

    public MessageService(HashSet<Socket> sockets) {
        this.sockets = sockets
            .stream()
            .collect(Collectors.toMap(socket -> socket, socket -> null));
    }

    public void open(Socket socket, String username) {}
}
