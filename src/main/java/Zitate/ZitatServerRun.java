package Zitate;

import Echo.EchoServerManager;

import java.io.IOException;

public class ZitatServerRun {
    public static void main(String args[]) throws IOException {
        System.err.println("Server started");
        int port = Integer.valueOf(args[0]);
        ZitatServerManager zitatServerManager = new ZitatServerManager(port);
    }
}
