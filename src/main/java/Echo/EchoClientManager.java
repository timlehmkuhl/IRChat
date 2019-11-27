package Echo;

import IRC.Actor;
import IRC.Transceiver.Transceiver;
import java.io.IOException;
import java.net.Socket;

public class EchoClientManager extends Thread implements Actor{
    private Socket socket;
    private Transceiver transceiver;


    public EchoClientManager(Socket socket) throws IOException {
        this.socket = socket;
        this.transceiver = new Transceiver(socket, this, true);
    }

    public void run() {
        transceiver.start();
    }



    public void request(String nachricht) throws IOException {
        tell(nachricht, null);
    }

    @Override
    public void tell(String message, Actor sender) throws IOException {
        transceiver.tell(message, null);
    }

    @Override
    public void shutdown() throws IOException {
        transceiver.shutdown();
        socket.close();
    }

}
