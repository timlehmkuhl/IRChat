package Echo;


import java.io.IOException;

public class EchoRun {
    public static void main(String args[]) throws IOException {
        System.err.println("Server started");
        int port = Integer.valueOf(args[0]);
        EchoMaster echoMaster = new EchoMaster(port);
    }
}
