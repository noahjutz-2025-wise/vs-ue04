import java.net.Socket;

public class RequestHandler {

    public static void handle(
        Socket socket,
        Protocol.Message request,
        MessageService service
    ) {
        switch (request) {
            case Protocol.Open req -> {
                service.open(socket, req.username());
            }
            case Protocol.Exit req -> {
                service.exit(socket);
            }
            case Protocol.Publ req -> {
                service.publ(socket, req.message());
            }
            default -> throw new IllegalStateException();
        }
    }
}
