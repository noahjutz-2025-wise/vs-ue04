import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

final char[] errorFrame = new char[] { 255 };

void main() {
    final var pool = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    );
    try (final var server = new ServerSocket()) {
        while (true) {
            try (final var socket = server.accept()) {
                pool.execute(() -> {
                    try (
                        final var r = new BufferedReader(
                            new InputStreamReader(socket.getInputStream())
                        );
                        final var w = new PrintWriter(socket.getOutputStream())
                    ) {
                        var request = new char[512];
                        if (r.read(request) < 1) {
                            w.write(errorFrame); // TODO return "malformed request"
                            return;
                        }
                    } catch (IOException e) {}
                });
            }
        }
    } catch (IOException e) {}
}
