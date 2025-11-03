import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

private final class Protocol {

    private interface Message {
    }

    private record Msg(String from, String to, String msg) {
    }

    private record ReqRegister(byte id, String username) implements Message {
    }

    private record ReqSend(byte id, Msg message) implements Message {
    }

    private record ReqGet(byte id, String username) implements Message {
    }

    private record ResStatus(byte id, byte code) implements Message {
    }

    private record ResGet(byte id, byte code, List<Msg> messages) implements
        Message {
    }

    static final byte TYPE_REGISTER = 0b00;
    static final byte TYPE_SEND = 0b01;
    static final byte TYPE_GET = 0b10;

    static final byte STATUS_OK = 0b00;
    static final byte STATUS_MALFORMED = 0b01;
    static final byte STATUS_ERROR = 0b11;

    private static final class Parser {

        static Message parse(DataInputStream din) throws IOException {
            final var type = din.readByte();
            return switch (type) {
                case TYPE_REGISTER -> parseRegister(din);
                case TYPE_SEND -> parseSend(din);
                case TYPE_GET -> parseGet(din);
                default -> {
                    yield null;
                }
            };
        }

        private static Message parseRegister(DataInputStream din)
            throws IOException {
            final var reqid = din.readByte();
            final var length = din.readByte();
            final var name = din.readNBytes(length);
            return new ReqRegister(
                reqid,
                new String(name, StandardCharsets.UTF_8)
            );
        }

        private static Message parseSend(DataInputStream din)
            throws IOException {
            final var reqid = din.readByte();
            final var userLength = din.readByte();
            final var username = din.readNBytes(userLength);
            final var recipLength = din.readByte();
            final var recip = din.readNBytes(recipLength);
            final var msgLength = ByteBuffer.wrap(din.readNBytes(2)).getInt();
            final var msg = din.readNBytes(msgLength);
            return new ReqSend(reqid, new Msg(
                new String(username, StandardCharsets.UTF_8),
                new String(recip, StandardCharsets.UTF_8),
                new String(msg, StandardCharsets.UTF_8)
            ));
        }

        private static Message parseGet(DataInputStream din) throws IOException {
            final var reqid = din.readByte();
            final var length = din.readByte();
            final var username = din.readNBytes(length);
            return new ReqGet(reqid, new String(username, StandardCharsets.UTF_8));
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
                        final var r = new DataInputStream(socket.getInputStream());
                        final var w = new BufferedOutputStream(socket.getOutputStream())
                    ) {
                        switch (Protocol.Parser.parse(r)) {
                            case Protocol.ReqRegister req -> {
                                chat.users.add(req.username);
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
