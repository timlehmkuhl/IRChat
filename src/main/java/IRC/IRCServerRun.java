package IRC;

import java.io.IOException;

public class IRCServerRun {
    public static void main(String args[]) throws IOException {
        System.err.println("Server started");
        int port = Integer.valueOf(args[0]);
        IRCServerManager ircServerManager = new IRCServerManager(port);
    }
}
