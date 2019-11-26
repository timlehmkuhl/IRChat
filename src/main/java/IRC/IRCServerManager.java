package IRC;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class IRCServerManager {

    private List<User> users = new ArrayList<User>();
    private ServerSocket serverSocket;
    private String host = InetAddress.getLocalHost().toString();

    private STGroup templates = new STGroupFile("G:\\InfTest\\IRC2\\src\\main\\java\\replies.stg");

    public IRCServerManager(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        request();
    }


    public void request() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();
            new ClientManager(socket, this).start();
        }
    }

    public String[] getParameters(String nachricht){
        String str = nachricht.substring(nachricht.indexOf(" ")+1);
        return str.split(" ");
    }

    /**
     * Einen neuen User adden
     */
    public String addUser(String nick, String username, String fullname, ClientManager clientManager)  {
        User newUser = new User(nick, username, fullname, clientManager, true);
        for (User u : users) {
            if (u.equals(newUser) && u.isRegister()){
                ST st462 = templates.getInstanceOf("ERR_ALREADYREGISTRED");
                return st462.render();
            }

            if (u.equals(newUser)) {
                u.setName(username);
                u.setRegister(true);
                u.setNick(nick);
                ST st001 = templates.getInstanceOf("welcome");
                st001.add("nick", nick).add("user", username).add("host", clientManager.getClientAdress());
                return st001.render();
            }
        }
        users.add(newUser);
        clientManager.setUser(newUser);
        ST st001 = templates.getInstanceOf("welcome");
        st001.add("nick", nick).add("user", username).add("host", clientManager.getClientAdress());
        return st001.render();
    }

    public String nick(String nick, User sender) throws IOException {
        if (nick.length() == 0) {
            ST st431 = templates.getInstanceOf("ERR_NONICKNAMEGIVEN");
            return st431.render();
        }
        if (existNick(nick)) {
            ST st433 = templates.getInstanceOf("ERR_NICKNAMEINUSE");
            return st433.add("nick", nick).render();
        }
        for (User u : users) {
            if (u.equals(sender) && u.isRegister()) {
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

    public boolean sendPrivateMessage(String tragetNick, String message, User sender) throws IOException {
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

        ST ret = templates.getInstanceOf("Send_PRIVMSG");
        for (User u : users) {
            if (u.getNick().equals(tragetNick)) {
                ret.add("nick", sender.getNick()).add("user", sender.getName()).add("host", sender.getAddress()).add("targetnick", tragetNick).add("nachricht", message);
                u.sendMessage(ret.render());
                return true;
            }
        }
        ST st401 = templates.getInstanceOf("ERR_NOSUCHNICK");
        sender.sendMessage(st401.add("nick", tragetNick).render());
        return false;
    }


    public boolean existNick(String nick) {
        for (User u : users) {
            if (u.getNick().equals(nick))
                return true;
        }
        return false;
    }


 /*   public void notice(String nick, String message, User sender) throws IOException {
        String head = ":" + sender.getNick() + "!" + sender.getAddress() + " NOTICE";
        for (User u : users) {
            if (u.getNick().equals(nick)) {
                u.sendMessage(head + " :" + message);
            }
        }
    }*/



  /*  public void serverMessages(String message) throws IOException {
        for (User u : users) {
            u.sendMessage(host + ":" + message);
        }
    }*/

 /*   public String pong() {
        return host + " PONG";
    }*/



 /*   public void removeUser(User user, String message) throws IOException {
        user.getClientManagerThread().interrupt();
        users.remove(user);
        serverMessages(message);
    }*/
}
