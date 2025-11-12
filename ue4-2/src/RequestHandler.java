import Protocol.Message;

public class RequestHandler {

    public static void handle(Message request) {
        switch (request) {
            case Protocol.Open req -> {}
            case Protocol.Close req -> {}
            case Protocol.Ping req -> {}
            case Protocol.Pong req -> {}
            case Protocol.Error req -> {}
            case Protocol.Unknown req -> {}
        }
    }
}
