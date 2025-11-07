import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class Protocol {

    public record Msg(String from, String to, String msg) {}

    public sealed interface Message
        permits
            Message.ReqRegister,
            Message.ReqSend,
            Message.ReqGet,
            Message.ResStatus,
            Message.ResGet {
        byte id();

        public record ReqRegister(byte id, String username) implements
            Message {}

        public record ReqSend(byte id, Msg message) implements Message {}

        public record ReqGet(byte id, String username) implements Message {}

        public record ResStatus(byte id, byte code, String message) implements
            Message {
            public ResStatus(byte id, byte code) {
                this(id, code, "");
            }
        }

        public record ResGet(byte id, byte code, List<Msg> messages) implements
            Message {}
    }

    static final byte TYPE_REQ_REGISTER = 0;
    static final byte TYPE_REQ_SEND = 1;
    static final byte TYPE_REQ_GET = 2;
    static final byte TYPE_RES_STATUS = 3;
    static final byte TYPE_RES_GET = 4;

    static final byte STATUS_OK = 0;
    static final byte STATUS_MALFORMED = 1;
    static final byte STATUS_ERROR = 2;

    public static final class Decoder {

        public static Message parse(DataInputStream din) throws IOException {
            final var type = din.readByte();
            return switch (type) {
                case TYPE_REQ_REGISTER -> parseRegister(din);
                case TYPE_REQ_SEND -> parseSend(din);
                case TYPE_REQ_GET -> parseGet(din);
                default -> throw new UnsupportedOperationException();
            };
        }

        private static Message parseRegister(DataInputStream din)
            throws IOException {
            final var reqid = din.readByte();
            final var length = din.readByte();
            final var name = din.readNBytes(length);
            return new Message.ReqRegister(
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
            final var msgLength = (din.readByte() << 8) | din.readByte();
            final var msg = din.readNBytes(msgLength);
            return new Message.ReqSend(
                reqid,
                new Msg(
                    new String(username, StandardCharsets.UTF_8),
                    new String(recip, StandardCharsets.UTF_8),
                    new String(msg, StandardCharsets.UTF_8)
                )
            );
        }

        private static Message parseGet(DataInputStream din)
            throws IOException {
            final var reqid = din.readByte();
            final var length = din.readByte();
            final var username = din.readNBytes(length);
            return new Message.ReqGet(
                reqid,
                new String(username, StandardCharsets.UTF_8)
            );
        }
    }

    public static final class Encoder {

        public static byte[] encode(Message.ResStatus res) throws IOException {
            final var bos = new ByteArrayOutputStream();
            bos.write(TYPE_RES_STATUS);
            bos.write(res.id());
            bos.write(res.code());

            final var msg = res.message.getBytes(StandardCharsets.UTF_8);

            bos.write(msg.length);
            bos.write(msg);

            return bos.toByteArray();
        }

        public static byte[] encode(Message.ResGet res) throws IOException {
            final var bos = new ByteArrayOutputStream();

            bos.write(TYPE_RES_GET);
            bos.write(res.id());
            bos.write(res.code());
            bos.write(res.messages.size());

            for (var msg : res.messages) {
                final var from = msg.from.getBytes(StandardCharsets.UTF_8);
                final var to = msg.to.getBytes(StandardCharsets.UTF_8);
                final var m = msg.msg.getBytes(StandardCharsets.UTF_8);

                bos.write(from.length);
                bos.write(from);
                bos.write(to.length);
                bos.write(to);
                bos.write(m.length >>> 8);
                bos.write(m.length);
                bos.write(m);
            }

            return bos.toByteArray();
        }
    }
}
