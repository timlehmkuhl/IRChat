package IRC;

import java.io.IOException;

public class IRCRun {
    public static void main(String args[]) throws IOException {
        int port = Integer.valueOf(args[0]);
        System.err.print("Server started! Port: " + port);
        IRCMaster ircMaster = new IRCMaster(port);

    }
}
