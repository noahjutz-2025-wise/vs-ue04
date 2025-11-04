import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class Protocol {

    public interface Message {}

    public record Msg(String from, String to, String msg) {}

    public record ReqRegister(byte id, String username) implements Message {}

    public record ReqSend(byte id, Msg message) implements Message {}

    public record ReqGet(byte id, String username) implements Message {}

    public record ResStatus(byte id, byte code) implements Message {}

    public record ResGet(byte id, byte code, List<Msg> messages) implements
        Message {}

    static final byte TYPE_REGISTER = 0b00;
    static final byte TYPE_SEND = 0b01;
    static final byte TYPE_GET = 0b10;

    static final byte STATUS_OK = 0b00;
    static final byte STATUS_MALFORMED = 0b01;
    static final byte STATUS_ERROR = 0b11;

    public static final class Parser {

        public static Message parse(DataInputStream din) throws IOException {
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
            return new ReqSend(
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
            return new ReqGet(
                reqid,
                new String(username, StandardCharsets.UTF_8)
            );
        }
    }

    public static final class Encoder {

        public byte[] encode(ResStatus res) {
            return new byte[] { res.id, res.code };
        }

        public byte[] encode(ResGet res) throws IOException {
            var bos = new ByteArrayOutputStream();

            bos.write(
                new byte[] { res.id, res.code, (byte) res.messages.size() }
            );

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
