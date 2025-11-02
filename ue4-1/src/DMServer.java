import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

final class Frames {

    static final byte TYPE_REGISTER = 0b00;
    static final byte TYPE_SEND = 0b01;
    static final byte TYPE_GET = 0b10;
    static final int TYPE_SHIFT = 6;

    static final byte STATUS_OK = 0b00;
    static final byte STATUS_MALFORMED = 0b01;
    static final byte STATUS_ERROR = 0b11;
    static final int STATUS_SHIFT = 4;
}

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
                            w.write(0b1111 << 4); // TODO return "malformed request"
                            return;
                        }
                    } catch (IOException e) {}
                });
            }
        }
    } catch (IOException e) {}
}
