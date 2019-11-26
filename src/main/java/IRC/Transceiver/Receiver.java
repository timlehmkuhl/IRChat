package IRC.Transceiver;

import IRC.ClientManager;

import java.io.*;
import java.net.Socket;

public class Receiver implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private boolean server;
    private ClientManager clientManager;

    public Receiver(Socket socket, ClientManager clientManager, boolean server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.clientManager = clientManager;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        String nachricht;
        try {
        while(!(nachricht = in.readLine()).equals("\u0004")) {
            clientManager.request(nachricht);
            System.err.println(nachricht);
            if(!server)
            clientManager.tell(nachricht, null);
        }
        socket.shutdownInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
