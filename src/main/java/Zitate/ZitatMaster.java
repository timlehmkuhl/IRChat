package Zitate;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ZitatMaster {

    private ServerSocket serverSocket;


    public ZitatMaster(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        request();
    }


    public void request() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();
            new ZitatSlave(socket).start();
        }
    }
}
