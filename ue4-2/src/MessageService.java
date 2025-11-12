import Protocol.Admn;
import java.io.IOException;
import java.io.PrintWriter;
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
        final var msg = new Protocol.Show(sockets.get(socket), message);
        dispatch(msg);
    }

    private void dispatch(Protocol.Message msg) {
        for (final var socket : sockets.keySet()) {
            try (var out = new PrintWriter(socket.getOutputStream())) {
                final var enc = switch (msg) {
                    case Protocol.Admn m -> Protocol.encode(m);
                    case Protocol.Show m -> Protocol.encode(m);
                    default -> throw new IllegalArgumentException();
                };
                out.println(enc);
            } catch (IOException e) {}
        }
    }
}
