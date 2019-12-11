package IRC;


import IRC.Antlr.IRCLexer;
import IRC.Antlr.IRCParser;
import IRC.Transceiver.Actor;
import IRC.Transceiver.Transceiver;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.net.Socket;

public class IRCSlave extends Thread implements Actor {

    private IRCMaster ircMaster;
    private Socket socket;
    private Transceiver transceiver;
    private User user;
    private String clientAdress;


    public IRCSlave(Socket socket, IRCMaster ircMaster) throws IOException {
        this.socket = socket;
        this.ircMaster = ircMaster;
        this.transceiver = new Transceiver(socket, this, true);
        this.clientAdress = socket.getRemoteSocketAddress().toString().substring(1);
        user = new User(null, null, clientAdress, this, false);
    }

    /**
     * Startet einen transceiver Slave Thread
     */
    public void run() {
        transceiver.start();
    }


    /**
     * Verarbeitet Befehle und fuert daraufhin gewuenschte Aktionen aus.
     * @param nachricht
     * @throws IOException
     */
    public void request(String nachricht) throws IOException {

        runAntlr(nachricht);

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

    public String getClientAdress() {
        return clientAdress;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void runAntlr(String in){
        CharStream input = CharStreams.fromString(in);
        IRCLexer lexer = new IRCLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        IRCParser parser = new IRCParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.irc();
        ParseTreeWalker walker = new ParseTreeWalker();
        IRCCommands converter = new IRCCommands(ircMaster, this);
        walker.walk(converter, tree);
    }

    public User getUser() {
        return user;
    }
}
