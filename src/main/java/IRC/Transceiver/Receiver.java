package IRC.Transceiver;

import Echo.EchoClientManager;
import IRC.ClientManager;
import Zitate.ZitatClientManager;

import java.io.*;
import java.net.Socket;

public class Receiver implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private boolean server;
    private ClientManager clientManager;
    private EchoClientManager echoClientManager;
    private ZitatClientManager zitatClientManager;

    public Receiver(Socket socket, ClientManager clientManager, boolean server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.clientManager = clientManager;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Receiver(Socket socket, EchoClientManager echoClientManager, boolean server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.echoClientManager = echoClientManager;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Receiver(Socket socket, ZitatClientManager zitatClientManager, boolean server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.zitatClientManager = zitatClientManager;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        String nachricht;
        try {
        while(!(nachricht = in.readLine()).equals("\u0004")) {
            if(clientManager != null)
            clientManager.request(nachricht);
            if(echoClientManager != null)
            echoClientManager.request(nachricht);
            if(zitatClientManager != null)
                zitatClientManager.request(nachricht);
            System.err.println(nachricht);
            if(!server) {
                if(clientManager != null)
                clientManager.tell(nachricht, null);
                if(echoClientManager != null)
                echoClientManager.tell(nachricht, null);
                if(zitatClientManager != null)
                    zitatClientManager.tell(nachricht, null);
            }
        }
        socket.shutdownInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
