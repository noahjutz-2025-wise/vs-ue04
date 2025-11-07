void main() {
    final var service = new MessageService();
    try (
        final var pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );
        final var server = new ServerSocket(1225)
    ) {
        while (true) {
            final var socket = server.accept();
            pool.execute(() -> {
                try (
                    socket;
                    final var r = new DataInputStream(socket.getInputStream());
                    final var w = new BufferedOutputStream(
                        socket.getOutputStream()
                    )
                ) {
                    final byte[] res = switch (Protocol.Decoder.parse(r)) {
                        case Protocol.ReqRegister req -> {
                            service.addUser(req.username());
                            yield Protocol.Encoder.encode(
                                new Protocol.ResStatus(
                                    req.id(),
                                    Protocol.STATUS_OK
                                )
                            );
                        }
                        case Protocol.ReqGet req -> {
                            final var messages = service.get(req.username());
                            yield Protocol.Encoder.encode(
                                new Protocol.ResGet(
                                    req.id(),
                                    Protocol.STATUS_OK,
                                    messages
                                )
                            );
                        }
                        case Protocol.ReqSend req -> {
                            service.send(req.message());
                            yield Protocol.Encoder.encode(
                                new Protocol.ResStatus(
                                    req.id(),
                                    Protocol.STATUS_OK
                                )
                            );
                        }
                        default -> throw new IllegalStateException();
                    };

                    w.write(res);
                    w.flush();
                } catch (IOException e) {
                    IO.println(e);
                    e.printStackTrace();
                }
            });
        }
    } catch (IOException e) {
        IO.println(e);
    }
}
