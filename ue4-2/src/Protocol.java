public class Protocol {

    public static void parse(String line) {
        if (line.length() > 1024) {
            line = line.substring(0, 1024);
        }

        final var components = line.split("#");

        if (components.length <= 1 && !line.equals("EXIT")) {
            throw new IllegalArgumentException(
                "Invalid message format: " + line
            );
        }

        switch (components[0]) {
            case "OPEN" -> {}
            case "EXIT" -> {}
            case "PUBL" -> {}
            case "SHOW" -> {}
            case "ADMN" -> {}
        }
    }
}
