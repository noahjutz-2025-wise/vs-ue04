public class Protocol {

    public sealed interface Message permits Open, Exit, Publ, Show, Admn {}

    public record Open(String username) implements Message {}

    public record Exit() implements Message {}

    public record Publ(String message) implements Message {}

    public record Show(String from, String message) implements Message {}

    public record Admn(String message) implements Message {}

    public static Message parse(String line) {
        if (line.length() > 1024) {
            line = line.substring(0, 1024);
        }

        final var components = line.split("#");

        if (components.length <= 1 && !line.equals("EXIT")) {
            throw new IllegalArgumentException(
                "Invalid message format: " + line
            );
        }

        return switch (components[0]) {
            case "OPEN" -> new Open(components[1]);
            case "EXIT" -> new Exit();
            case "PUBL" -> new Publ(components[1]);
            default -> throw new IllegalArgumentException(
                "Invalid operation: " + components[1]
            );
        };
    }

    public static String encode(Show message) {
        assert !message.from().contains("#");
        assert !message.from().contains("\n");
        assert !message.message().contains("#");
        assert !message.message().contains("\n");
        return "SHOW#" + message.from() + "#" + message.message();
    }

    public static String encode(Admn message) {
        assert !message.message().contains("#");
        assert !message.message().contains("\n");
        return "ADMN#" + message.message();
    }
}
