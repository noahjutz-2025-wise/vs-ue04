import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class MessagingService {
  private static MessageStore store = new MessageStore();

  static void main() {
    try (final var e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final var s = new ServerSocket(8080, 1, InetAddress.getByName("0.0.0.0")); ) {
      while (true) {
        final var c = s.accept();
        e.execute(
            () -> {
              try (c;
                  final var r = new BufferedReader(new InputStreamReader(c.getInputStream()));
                  final var w = new PrintWriter(c.getOutputStream())) {
                final var sb = new StringBuilder();
                while (true) {
                  final var l = r.readLine();
                  if (l == null || l.isEmpty()) break;
                  sb.append(l).append("\n");
                }
                handleRequest(c, r, w, sb.toString());
              } catch (IOException _) {
              }
            });
      }
    } catch (IOException _) {
    }
  }

  private static void handleRequest(Socket c, BufferedReader r, PrintWriter w, String req) {
    final var lines = req.split("\n");
    switch (lines[0]) {
      case "REGISTER" -> {
        store.addUser(lines[1]);
        w.println("OK");
      }
      case "SEND" -> {
        store.addMessage(
            lines[1],
            lines[2],
            String.join("\n", Arrays.stream(lines).toList().subList(3, lines.length)));
        w.println("OK");
      }
      case "GET" -> {
        final var msgs = store.getMessagesTo(lines[1]);
        for (final var msg : msgs) {
          w.println(msg.from());
          w.println(msg.to());
          w.println(msg.body());
          w.println();
        }
      }
    }
  }
}
