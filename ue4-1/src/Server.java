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
                        final Message res = switch (Protocol.Decoder.parse(r)) {
                            case Protocol.ReqRegister req -> {
                                chat.users.add(req.username());
                                return new Protocol.ResStatus(
                                    req.id(),
                                    Protocol.STATUS_OK
                                );
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

                        w.write(Protocol.Encoder.encode(res));
                        w.flush();
                    } catch (IOException e) {}
                });
            }
        }
    } catch (IOException e) {}
}
