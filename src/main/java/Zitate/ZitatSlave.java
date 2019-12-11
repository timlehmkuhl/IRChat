package Zitate;

import IRC.Transceiver.Actor;
import IRC.Transceiver.Transceiver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZitatSlave extends Thread implements Actor{
    private Socket socket;
    private Transceiver transceiver;


    public ZitatSlave(Socket socket) throws IOException {
        this.socket = socket;
        this.transceiver = new Transceiver(socket, this, true);
    }

    public void run() {
        transceiver.start();
    }



    public void request(String nachricht) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader;

        reader = new BufferedReader(new FileReader(
                "/Users/timmichaellehmkuhl/InfProjekte/irc2/src/main/java/IRC/Transceiver/zitate.txt"));
        String line = reader.readLine();
        while (line != null) {

            lines.add(line);
            // read next line
            line = reader.readLine();
        }
        reader.close();

        Random r = new Random();
        int random;
        while (true){
            random = r.nextInt(lines.size());
            if(random % 2 != 1) break;
        }
        String s= lines.get(random) + "\n" + lines.get(random+1);

        tell(s,null);
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
