package IRC;

import IRC.Antlr.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class IRCMaster {

    private List<User> users = new ArrayList<User>();
    private ServerSocket serverSocket;
    String host = InetAddress.getLocalHost().getHostAddress();

    private STGroup templates = new STGroupFile("/Users/timmichaellehmkuhl/InfProjekte/irc2/src/main/java/replies.stg");

    public IRCMaster(int port) throws IOException {
        System.err.println(" IP: " + host);
        this.serverSocket = new ServerSocket(port);
        request();
    }

    /**
     * Wartet auf Clienten und erstellt Slaves
     * @throws IOException
     */
    public void request() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();
            new IRCSlave(socket, this).start();
        }
    }


    /**
     * Fuegt einen neuen, noch nicht bestehenden User hinzu.
     * Wird von den Salves aufgerufen
     * @param nick
     * @param username
     * @param fullname
     * @param IRCSlave
     * @return
     */
    public String addUser(String nick, String username, String fullname, IRCSlave IRCSlave)  {
        User newUser = new User(nick, username, fullname, IRCSlave, true);
        for (User u : users) {
            if (u.equals(newUser) && u.isRegistered()){
                ST st462 = templates.getInstanceOf("ERR_ALREADYREGISTRED");
                return st462.render();
            }

            if (u.equals(newUser)) {
                u.setName(username);
                u.setRegister(true);
                u.setNick(nick);
                ST st001 = templates.getInstanceOf("welcome");
                st001.add("nick", nick).add("user", username).add("host", IRCSlave.getClientAdress());
                return st001.render();
            }
        }
        users.add(newUser);
        IRCSlave.setUser(newUser);
        ST st001 = templates.getInstanceOf("welcome");
        st001.add("nick", nick).add("user", username).add("host", IRCSlave.getClientAdress());
        return st001.render();
    }

    /**
     * Prueft auf neue, gueltige Nicknamen und reserviert sie
     * @param nick
     * @param sender
     * @return
     */
    public String nick(String nick, User sender) {
        if (nick.length() == 0) {
            ST st431 = templates.getInstanceOf("ERR_NONICKNAMEGIVEN");
            return st431.render();
        }
        if (existNick(nick)) {
            ST st433 = templates.getInstanceOf("ERR_NICKNAMEINUSE");
            return st433.add("nick", nick).render();
        }
        for (User u : users) {
            if (u.equals(sender) && u.isRegistered()) {
                sender.setNick(nick);
                ST stChangeNick = templates.getInstanceOf("changenick");
                return stChangeNick.add("nick", nick).render();
            }
//
        }
        sender.setNick(nick);
        users.add(sender);
        ST stNick = templates.getInstanceOf("nick");
      return stNick.add("nick", nick).render() ;
    }


    /**
     * Uebergibt einen bestehenden User eine Nachricht eines anderen Users
     * @param tragetNick
     * @param message
     * @param sender
     * @param notice
     * @return
     * @throws IOException
     */
    public boolean sendPrivateMessage(String tragetNick, String message, User sender, boolean notice) throws IOException {
        System.err.println(message);
        if (message.length() == 0) {
            ST st412 = templates.getInstanceOf("ERR_NOTEXTTOSEND");
            sender.sendMessage(st412.render());
            return false;
        }
        if (tragetNick.length() == 0) {
            ST st411 = templates.getInstanceOf("ERR_NORECIPIENT");
            sender.sendMessage(st411.add("command", tragetNick).render());
            return false;
        }

        for (User u : users) {
            if (u.getNick().equals(tragetNick)) {
                if(!notice) {
                    ST ret = templates.getInstanceOf("Send_PRIVMSG");
                    ret.add("nick", sender.getNick()).add("user", sender.getName()).add("host", sender.getAddress()).add("targetnick", tragetNick).add("nachricht", message);
                    u.sendMessage(ret.render());
                } else if(notice){
                    ST ret = templates.getInstanceOf("Send_NOTICE");
                    ret.add("nick", sender.getNick()).add("user", sender.getName()).add("host", sender.getAddress()).add("nachricht", message);
                    u.sendMessage(ret.render());
                }
                return true;
            }
        }
        ST st401 = templates.getInstanceOf("ERR_NOSUCHNICK");
        sender.sendMessage(st401.add("nick", tragetNick).render());
        return false;
    }


    /**
     * Prueft ob bereits ein Nickname existiert
     * @param nick
     * @return
     */
    public boolean existNick(String nick) {
        for (User u : users) {
            if (u.getNick().equals(nick))
                return true;
        }
        return false;
    }


    /**
     * Schickt ein Pong
     * @return
     */
    public String pong() {
        ST p = templates.getInstanceOf("Pong");
        return p.add("host", host).render();
    }



   public void removeUser(User user, String message) throws IOException {
       ST q = templates.getInstanceOf("Quit");
       q.add("nick", user.getNick()).add("user", user.getName()).add("host", host).add("nachricht", message).add("clienthost", user.getAddress());
        user.getIRCSlave().interrupt();
        users.remove(user);
       for (User u : users) {
           u.sendMessage(q.render());
       }
    }



}
