package IRC;


import IRC.Transceiver.Actor;
import IRC.Transceiver.Transceiver;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.net.Socket;

public class IRCSlave extends Thread implements Actor {

    private IRCMaster ircMaster;
    private Socket socket;
    private Transceiver transceiver;
    private User user;
    private String clientAdress;
    private STGroup templates = new STGroupFile("/Users/timmichaellehmkuhl/InfProjekte/irc2/src/main/java/replies.stg");

    public IRCSlave(Socket socket, IRCMaster ircMaster) throws IOException {
        this.socket = socket;
        this.ircMaster = ircMaster;
        this.transceiver = new Transceiver(socket, this, true);
        this.clientAdress = socket.getRemoteSocketAddress().toString().substring(1);
        user = new User(null, null, clientAdress, this, false);
    }

    public void run() {
        transceiver.start();
    }

    public String[] getParameters(String nachricht){
        String str = nachricht.substring(nachricht.indexOf(" ")+1);
        return str.split(" ");
    }

    public String getAllInOneParameters(String nachricht, int offset){
        String str = nachricht.substring(nachricht.indexOf(" ")+1);
        String split[] = str.split(" ");
        String ret = "";
        for(int i = offset; i < split.length; i++)
            ret += split[i] + " ";
        return ret.trim();
    }


    public void request(String nachricht) throws IOException {
        String[] parameters = getParameters(nachricht);

        if (nachricht.startsWith("NICK")) {

            String ret = ircMaster.nick(parameters[0], user);
            tell(ret, null);
        }

        else if (nachricht.startsWith("USER")) {
            if(parameters.length < 2){
                ST st461 = templates.getInstanceOf("ERR_NEEDMOREPARAMS");
                tell(st461.add("command", "USER").render(), null);
                return;
            }
            tell(ircMaster.addUser(parameters[0],
                    parameters[1], clientAdress, this), null);
        }

        else if (user.isRegister() && nachricht.startsWith("PRIVMSG")) {
            if(parameters.length < 2){
                ST st461 = templates.getInstanceOf("ERR_NEEDMOREPARAMS");
                tell(st461.add("command", "PRIVMSG").render(), null);
                return;
            }
            ircMaster.sendPrivateMessage(parameters[0], getAllInOneParameters(nachricht, 1), user);
        } else {
            ST st421 = templates.getInstanceOf("ERR_UNKNOWNCOMMAND");
            tell(st421.add("command", nachricht).render(), null);
        }

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
}
