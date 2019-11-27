package Echo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class EchoMaster {
    private ServerSocket serverSocket;


    public EchoMaster(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        request();
    }


    public void request() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();
            new EchoSlave(socket).start();
        }
    }


}
