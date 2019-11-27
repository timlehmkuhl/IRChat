package Echo;


import java.io.IOException;

public class EchoServerRun {
    public static void main(String args[]) throws IOException {
        System.err.println("Server started");
        int port = Integer.valueOf(args[0]);
        EchoServerManager echoServerManager = new EchoServerManager(port);
    }
}
