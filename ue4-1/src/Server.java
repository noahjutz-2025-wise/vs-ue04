private static final class MessageServer {

    final List<String> users = new ArrayList<>();
}

void main() {
    final var chat = new MessageServer();
    try (
        final var pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );
        final var server = new ServerSocket()
    ) {
        while (true) {
            try (final var socket = server.accept()) {
                pool.execute(() -> {
                    try (
                        final var r = new DataInputStream(
                            socket.getInputStream()
                        );
                        final var w = new BufferedOutputStream(
                            socket.getOutputStream()
                        )
                    ) {
                        final byte[] res = switch (Protocol.Decoder.parse(r)) {
                            case Protocol.ReqRegister req -> {
                                chat.users.add(req.username());
                                yield Protocol.Encoder.encode(
                                    new Protocol.ResStatus(
                                        req.id(),
                                        Protocol.STATUS_OK
                                    )
                                );
                            }
                            case Protocol.ReqGet req -> {
                                // TODO get
                                // TODO reply
                                throw new UnsupportedOperationException();
                            }
                            case Protocol.ReqSend req -> {
                                // TODO send
                                // TODO reply
                                throw new UnsupportedOperationException();
                            }
                            case null -> {
                                // TODO reply
                                throw new UnsupportedOperationException();
                            }
                            default -> throw new IllegalStateException();
                        };

                        w.write(res);
                        w.flush();
                    } catch (IOException e) {}
                });
            }
        }
    } catch (IOException e) {}
}
