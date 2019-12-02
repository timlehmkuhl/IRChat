package Echo;


import java.io.IOException;

public class EchoRun {
    public static void main(String args[]) throws IOException {
        int port = Integer.valueOf(args[0]);
        System.err.print("Server started! Port: " + port);
        EchoMaster echoMaster = new EchoMaster(port);
    }
}
