package Zitate;

import java.io.IOException;

public class ZitatRun {
    public static void main(String args[]) throws IOException {
        System.err.println("Server started");
        int port = Integer.valueOf(args[0]);
        ZitatMaster zitatMaster = new ZitatMaster(port);
    }
}
