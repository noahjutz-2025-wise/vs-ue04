import Protocol.Message;

public class RequestHandler {

    public static void handle(Message request) {
        switch (request) {
            case Protocol.Open req -> {}
            case Protocol.Exit req -> {}
            case Protocol.Publ req -> {}
            case Protocol.Admn req -> {}
        }
    }
}
