void main() {
    final var service = new MessageService();
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
                                service.users.add(req.username());
                                yield Protocol.Encoder.encode(
                                    new Protocol.ResStatus(
                                        req.id(),
                                        Protocol.STATUS_OK
                                    )
                                );
                            }
                            case Protocol.ReqGet req -> {
                                final var messages = service.messages
                                    .stream()
                                    .filter(msg ->
                                        msg.to().equals(req.username())
                                    )
                                    .toList();
                                yield Protocol.Encoder.encode(
                                    new Protocol.ResGet(
                                        req.id(),
                                        Protocol.STATUS_OK,
                                        messages
                                    )
                                );
                            }
                            case Protocol.ReqSend req -> {
                                service.messages.add(req.message());
                                yield Protocol.Encoder.encode(
                                    new Protocol.ResStatus(
                                        req.id(),
                                        Protocol.STATUS_OK
                                    )
                                );
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
