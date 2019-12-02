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
    private STGroup templates = new STGroupFile("G:\\InfTest\\IRC2\\src\\main\\java\\replies.stg");

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
     * Gibt die Parameter eines Befehls zurueck
     * @param nachricht
     * @return
     */
    public String[] getParameters(String nachricht){
        String str = nachricht.substring(nachricht.indexOf(" ")+1);
        return str.split(" ");
    }

    /**
     * Gibt alle Parameter eines Befehls ab dem offset in einem String zurueck
     * @param nachricht
     * @param offset
     * @return
     */
    public String getAllInOneParameters(String nachricht, int offset){
        String str = nachricht.substring(nachricht.indexOf(" ")+1);
        String split[] = str.split(" ");
        String ret = "";
        for(int i = offset; i < split.length; i++)
            ret += split[i] + " ";
        return ret.trim();
    }

    /**
     * Verarbeitet Befehle und fuert daraufhin gewuenschte Aktionen aus.
     * @param nachricht
     * @throws IOException
     */
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

        else if (user.isRegistered() && nachricht.startsWith("PRIVMSG")) {
            if(parameters.length < 2){
                ST st461 = templates.getInstanceOf("ERR_NEEDMOREPARAMS");
                tell(st461.add("command", "PRIVMSG").render(), null);
                return;
            }
            ircMaster.sendPrivateMessage(parameters[0], getAllInOneParameters(nachricht, 1), user, false);
        }  else
        if (user.isRegistered() && nachricht.startsWith("NOTICE")) {
            ircMaster.sendPrivateMessage(parameters[0], getAllInOneParameters(nachricht, 1), user, true);
        }
        else if (user.isRegistered() && nachricht.startsWith("QUIT")) {
            ircMaster.removeUser(user, parameters[0]);
            shutdown();
        } else
            if(user.isRegistered() && nachricht.startsWith("PING")){
            tell(ircMaster.pong(), null);
        } else if(user.isRegistered() && nachricht.startsWith("PONG")){

            }
        else {
            String s[] = nachricht.split(" ");
            ST st421 = templates.getInstanceOf("ERR_UNKNOWNCOMMAND");
            tell(st421.add("command", s[0]).render(), null);
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
