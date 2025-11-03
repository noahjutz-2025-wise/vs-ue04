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
                        final var r = new DataInputStream(socket.getInputStream());
                        final var w = new BufferedOutputStream(socket.getOutputStream())
                    ) {
                        switch (Protocol.Parser.parse(r)) {
                            case Protocol.ReqRegister req -> {
                                chat.users.add(req.username());
                                // TODO reply
                            }
                            case Protocol.ReqGet req -> {
                                // TODO get
                                // TODO reply
                            }
                            case Protocol.ReqSend req -> {
                                // TODO send
                                // TODO reply
                            }
                            case null -> {
                                // TODO reply
                            }
                            default -> throw new IllegalStateException();
                        }

                        w.flush();
                    } catch (IOException e) {
                    }
                });
            }
        }
    } catch (IOException e) {
    }
}
