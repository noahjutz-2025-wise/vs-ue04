import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MessageService {

    private final Map<Socket, String> sockets = new HashMap<>();
    private final Map<Socket, PrintWriter> socketWriters = new HashMap<>();

    public void open(Socket socket, String username) {
        IO.println("OPEN: " + username);
        try {
            final var out = new PrintWriter(socket.getOutputStream(), true);
            socketWriters.put(socket, out);
            sockets.put(socket, username);
            final var msg = new Protocol.Admn("Welcome, " + username + "!");
            dispatch(msg, null);
        } catch (IOException e) {}
    }

    public void exit(Socket socket) {
        IO.println("EXIT: " + sockets.get(socket));
        final var msg = new Protocol.Admn(
            "Goodbye, " + sockets.get(socket) + "!"
        );
        dispatch(msg, null);
        var writer = socketWriters.remove(socket);
        if (writer != null) {
            writer.close();
        }
        sockets.remove(socket);
    }

    public void publ(Socket socket, String message) {
        IO.println("PUBL: " + sockets.get(socket) + ": " + message);
        final var msg = new Protocol.Show(sockets.get(socket), message);
        dispatch(msg, socket);
    }

    private void dispatch(Protocol.Message msg, Socket ignore) {
        for (final var socket : sockets.keySet()) {
            if (socket == ignore) continue;
            var out = socketWriters.get(socket);
            if (out != null && !out.checkError()) {
                final var enc = switch (msg) {
                    case Protocol.Admn m -> Protocol.encode(m);
                    case Protocol.Show m -> Protocol.encode(m);
                    default -> throw new IllegalArgumentException();
                };
                out.println(enc);
                out.flush();
            }
        }
    }
}
