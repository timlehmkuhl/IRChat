package IRC.Transceiver;

import Echo.EchoSlave;
import IRC.IRCSlave;
import Zitate.ZitatSlave;

import java.io.*;
import java.net.Socket;

public class Receiver implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private boolean server;
    private IRCSlave IRCSlave;
    private EchoSlave echoSlave;
    private ZitatSlave zitatSlave;

    public Receiver(Socket socket, IRCSlave IRCSlave, boolean server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.IRCSlave = IRCSlave;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Receiver(Socket socket, EchoSlave echoSlave, boolean server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.echoSlave = echoSlave;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Receiver(Socket socket, ZitatSlave zitatSlave, boolean server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.zitatSlave = zitatSlave;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        String nachricht;
        try {
        while(!(nachricht = in.readLine()).equals("\u0004")) {
            if(IRCSlave != null)
                IRCSlave.request(nachricht);
            if(echoSlave != null)
                echoSlave.request(nachricht);
            if(zitatSlave != null)
                zitatSlave.request(nachricht);
            System.err.println(nachricht);
            if(!server) {
                if(IRCSlave != null)
                  IRCSlave.tell(nachricht, null);
                if(echoSlave != null)
                    echoSlave.tell(nachricht, null);
                if(zitatSlave != null)
                    zitatSlave.tell(nachricht, null);
            }
        }
        socket.shutdownInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
