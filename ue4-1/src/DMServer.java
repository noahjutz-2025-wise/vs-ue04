private final class Protocol {

    private interface Message {}

    private record Msg(String from, String to, String msg) {}

    private record ReqRegister(byte id, String username) implements Message {}

    private record ReqSend(byte id, Msg message) implements Message {}

    private record ReqGet(byte id, String username) implements Message {}

    private record ResStatus(byte id, byte code) implements Message {}

    private record ResGet(byte id, byte code, List<Msg> messages) implements
        Message {}

    static final byte TYPE_REGISTER = 0b00;
    static final byte TYPE_SEND = 0b01;
    static final byte TYPE_GET = 0b10;

    static final byte STATUS_OK = 0b00;
    static final byte STATUS_MALFORMED = 0b01;
    static final byte STATUS_ERROR = 0b11;

    private static final class Parser {

        Message parse(byte[] message) {
            return switch (message[0]) {
                case TYPE_REGISTER -> {
                    yield null;
                }
                default -> {
                    yield null;
                }
            };
        }
    }
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
                            case Protocol.TYPE_REGISTER -> {
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
