private final class Frames {

    static final byte TYPE_REGISTER = 0b00;
    static final byte TYPE_SEND = 0b01;
    static final byte TYPE_GET = 0b10;

    static final byte STATUS_OK = 0b00;
    static final byte STATUS_MALFORMED = 0b01;
    static final byte STATUS_ERROR = 0b11;
}

private final class MessageServer {

    final List<String> users = List.of();
}

void main() {
    final var chat = new MessageServer();
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
                        final var w = new BufferedOutputStream(
                            socket.getOutputStream()
                        )
                    ) {
                        var request = new char[512];
                        if (r.read(request) < 1) {
                            // TODO send invalid request error
                            return;
                        }

                        switch (request[0]) {
                            case Frames.TYPE_REGISTER -> {
                                var length = request[2];
                                chat.users.add(new String(request, 1, length));
                                // TODO send success response
                            }
                            default -> {
                                // TODO send invalid request error
                            }
                        }

                        w.flush();
                    } catch (IOException e) {}
                });
            }
        }
    } catch (IOException e) {}
}
