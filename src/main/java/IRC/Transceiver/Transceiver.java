package IRC.Transceiver;

import Echo.EchoClientManager;
import Echo.EchoServerManager;
import IRC.Actor;
import IRC.ClientManager;
import Zitate.ZitatClientManager;


import java.io.IOException;
import java.net.Socket;

public class Transceiver implements Actor {

    private Receiver receiver;
    private Transmitter transmitter;
    private Thread receiverThread;


    public Transceiver (Socket socket, ClientManager clientManager, boolean server) throws IOException {
        transmitter = new Transmitter(socket);
        receiver = (new Receiver(socket, clientManager, server));
        receiverThread = new Thread(receiver);
    }

    public Transceiver (Socket socket, EchoClientManager echoClientManager, boolean server) throws IOException {
        transmitter = new Transmitter(socket);
        receiver = (new Receiver(socket, echoClientManager, server));
        receiverThread = new Thread(receiver);
    }

    public Transceiver (Socket socket, ZitatClientManager zitatClientManager, boolean server) throws IOException {
        transmitter = new Transmitter(socket);
        receiver = (new Receiver(socket, zitatClientManager, server));
        receiverThread = new Thread(receiver);
    }

    public void start()  {
        receiverThread.start();
    }

    @Override
    public void tell(String message, Actor sender) throws IOException {
        transmitter.tell(message, sender);
    }

    @Override
    public void shutdown() throws IOException {
        transmitter.tell("\u0004", null);

    }

}
