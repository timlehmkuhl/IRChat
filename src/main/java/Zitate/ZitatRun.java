package Zitate;

import java.io.IOException;

public class ZitatRun {
    public static void main(String args[]) throws IOException {
        int port = Integer.valueOf(args[0]);
        System.err.print("Server started! Port: " + port);
        ZitatMaster zitatMaster = new ZitatMaster(port);
    }
}
