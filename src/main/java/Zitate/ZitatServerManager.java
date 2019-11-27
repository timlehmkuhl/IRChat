package Zitate;

import Echo.EchoClientManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ZitatServerManager {

    private ServerSocket serverSocket;


    public ZitatServerManager(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        request();
    }


    public void request() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();
            new ZitatClientManager(socket).start();
        }
    }
}
