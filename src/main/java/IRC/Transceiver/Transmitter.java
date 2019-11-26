package IRC.Transceiver;

import IRC.Actor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Transmitter implements Actor {

        private Socket socket;
        private PrintWriter writer;

        public Transmitter(Socket socket) throws IOException {
            this.socket = socket;
            writer = new PrintWriter(socket.getOutputStream(), true);
        }

        @Override
        public void tell(String message, Actor sender) {
            writer.println(message);
        }

        @Override
        public void shutdown() throws IOException {
            writer.println("/u0004");
            socket.shutdownOutput();
        }


}


