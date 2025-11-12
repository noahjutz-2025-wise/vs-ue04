import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

List<Socket> sockets = new ArrayList<>();

void main() {
    try (final var server = new ServerSocket(2345)) {
        while (true) {
            final var socket = server.accept();
            sockets.add(socket);
            new Thread(() -> {
                try (
                    final var in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                    );
                    final var out = new PrintWriter(socket.getOutputStream())
                ) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.println(line.toUpperCase());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })
                .start();
            sockets.remove(socket);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
