package IRC.Transceiver;

import Echo.EchoSlave;
import IRC.IRCSlave;
import Zitate.ZitatSlave;


import java.io.IOException;
import java.net.Socket;

public class Transceiver implements Actor {

    private Receiver receiver;
    private Transmitter transmitter;
    private Thread receiverThread;


    public Transceiver (Socket socket, IRCSlave IRCSlave, boolean server) throws IOException {
        transmitter = new Transmitter(socket);
        receiver = (new Receiver(socket, IRCSlave, server));
        receiverThread = new Thread(receiver);
    }

    public Transceiver (Socket socket, EchoSlave echoSlave, boolean server) throws IOException {
        transmitter = new Transmitter(socket);
        receiver = (new Receiver(socket, echoSlave, server));
        receiverThread = new Thread(receiver);
    }

    public Transceiver (Socket socket, ZitatSlave zitatSlave, boolean server) throws IOException {
        transmitter = new Transmitter(socket);
        receiver = (new Receiver(socket, zitatSlave, server));
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
    public void shutdown()  {
        transmitter.tell("\u0004", null);

    }

}
