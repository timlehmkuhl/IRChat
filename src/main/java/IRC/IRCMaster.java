package IRC;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRCMaster {

    private List<User> users = new ArrayList<User>();
    private ServerSocket serverSocket;
    String host = InetAddress.getLocalHost().getHostAddress();

    private List<Channel> channels = new ArrayList<Channel>();
    private List<User> channelUserList = new ArrayList<>();
    private Map<String, List<User>> channelUserMap = new HashMap<>();

    private STGroup templates = new STGroupFile("G:\\InfTest\\IRC2\\src\\main\\java\\replies.stg");

    public IRCMaster(int port) throws IOException {
        System.err.println(" IP: " + host);
        this.serverSocket = new ServerSocket(port);
     //   createChannel();
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

    private void createChannel(){
        Channel test = new Channel("#Test", "test Channel");
        Channel gaming = new Channel("#Gaming", "Gaming Channel");

        channels.add(test);
        channels.add(gaming);
        channelUserMap.put(test.getName(), users);
        channelUserMap.put(gaming.getName(), users);
     //   channelUser.put(gaming.getName(), users);

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
     * @param target
     * @param message
     * @param sender
     * @param notice
     * @return
     * @throws IOException
     */
    public boolean sendPrivateMessage(String target, String message, User sender, boolean notice) throws IOException {
        System.err.println(message);
        if (message.length() == 0) {
            ST st412 = templates.getInstanceOf("ERR_NOTEXTTOSEND");
            sender.sendMessage(st412.render());
            return false;
        }
        if (target.length() == 0) {
            ST st411 = templates.getInstanceOf("ERR_NORECIPIENT");
            sender.sendMessage(st411.add("command", target).render());
            return false;
        }

        for (User u : users) {
            if (u.getNick().equals(target)) {
                if(!notice) {
                    ST ret = templates.getInstanceOf("Send_PRIVMSG");
                    ret.add("nick", sender.getNick()).add("user", sender.getName()).add("host", sender.getAddress()).add("targetnick", target).add("nachricht", message);
                    u.sendMessage(ret.render());
                } else if(notice){
                    ST ret = templates.getInstanceOf("Send_NOTICE");
                    ret.add("nick", sender.getNick()).add("user", sender.getName()).add("host", sender.getAddress()).add("nachricht", message);
                    u.sendMessage(ret.render());
                }
                return true;
            }
        }

        //channel
        if(channelUserMap.containsKey(target)) {
            for (User u : channelUserMap.get(target)) {
                if(!notice) {
                    ST ret = templates.getInstanceOf("Send_PRIVMSG");
                    ret.add("nick", sender.getNick()).add("user", sender.getName()).add("host", sender.getAddress().replace(":", "")).add("targetnick", target).add("nachricht", message);
                    u.sendMessage(ret.render());
                } else if(notice){
                    ST ret = templates.getInstanceOf("Send_NOTICE");
                    ret.add("nick", sender.getNick()).add("user", sender.getName()).add("host", sender.getAddress().replace(":", "")).add("nachricht", message);
                    u.sendMessage(ret.render());
                }
            }
            return true;
        }
        if (!channelUserMap.containsKey(target)) {
            ST st404 = templates.getInstanceOf("ERR_CANNOTSENDTOCHAN");
            sender.sendMessage(st404.add("name", target).add("host", host).add("nick", sender.getNick()).render());
            return false;
        }

        ST st401 = templates.getInstanceOf("ERR_NOSUCHNICK");
        sender.sendMessage(st401.add("nick", target).render());
        return false;
    }

    /**
     * Fuegt einen User zu einem bestehenden Channel hinzu oder
     * erstellt einen nicht vorhandenen
     * @param channelName
     * @param sender
     * @return
     */
    public String join(String channelName, User sender)  {
        boolean flag = false;
        Channel channel = null;

        for (Channel c : channels) {
            if((c.getName().equals(channelName))) {
               flag = true;
            }
        }

        if(!flag){
           // ST st403 = templates.getInstanceOf("ERR_NOSUCHCHANNEL");
          //  return st403.add("name", channelName).render();

            Channel newChannel = new Channel(channelName, null);
            channels.add(newChannel);
            channelUserMap.put(newChannel.getName(), users);
            channel = newChannel;
        }
        channelUserList.add(sender);
        channelUserMap.put(channelName, channelUserList);


        for(Channel c : channels){
            if(c.getName().equals(channelName))
                channel = c;
            break;
        }
        // User List
        String users = "";
            for(Map.Entry<String,List<User>> entry : channelUserMap.entrySet()){
                if(entry.getKey().equals(channelName)) {
                    for (User u : entry.getValue()) {
                        users += u.getNick() + " ";

                    }
                }
            }
        ST topicST = templates.getInstanceOf("RPL_TOPIC");
        String topic = "";
        if(channel.getTopic() != null)
        topic = topicST.add("name", channelName).add("topic", channel.getTopic()).add("host", host).add("nick", sender.getNick()).render() + "\n";
        ST j = templates.getInstanceOf("join");
        ST st353 = templates.getInstanceOf("RPL_NAMEREPLY");
        ST st366 = templates.getInstanceOf("RPL_ENDOFNAMES");
        return j.add("host", host).add("nick", sender.getNick()).add("name", channelName).add("user", sender.getName()).render()
                + topic
                + st353.add("host", host).add("nick", sender.getNick()).add("name", channelName).add("user", sender.getName()).add("users", users.trim()).render()
                + st366.add("host", host).add("nick", sender.getNick()).add("name", channelName).add("user", sender.getName()).render();

    }

    /**
     * Entfernt einen User aus einem Channel
     * @param user
     * @param channel
     * @return
     * @throws IOException
     */
    public boolean leaveChannel(User user, String channel) throws IOException {
        if(!(channelUserMap.get(channel).contains(user))) {
            ST st442 = templates.getInstanceOf("ERR_NOTONCHANNEL");
            user.sendMessage(st442.add("name", channel).add("host", host).add("nick", user.getNick()).render());
            return false;
        }
        if(!(channelUserMap.containsKey(channel))) {
            ST st403 = templates.getInstanceOf("ERR_NOSUCHCHANNEL");
            user.sendMessage(st403.add("name", channel).render());
            return false;
        }

        ST leave = templates.getInstanceOf("leave_channel");
        user.sendMessage(leave.add("name", channel).render());
        channelUserMap.get(channel).remove(user);
        if(channelUserMap.get(channel).isEmpty()) channelUserMap.remove(channel);
        return true;
    }

    /**
     * Setzt die Topic eines Channels
     * @param channelName
     * @param messsage
     * @param sender
     * @return
     * @throws IOException
     */
    boolean setTopic(String channelName, String messsage, User sender) throws IOException {
        for (Channel c : channels) {
            if (c.getName().equals(channelName)) {
                    c.setTopic(messsage);
                    ST st332 = templates.getInstanceOf("RPL_TOPIC");
                    sender.sendMessage(st332.add("name", channelName).add("topic", messsage).render());
                    return true;
            }
        }
        ST st442 = templates.getInstanceOf("ERR_NOTONCHANNEL");
        sender.sendMessage(st442.add("name", channelName).add("host", host).add("nick", sender.getNick()).render());
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
        if(message == null || message.equals("")) message = " ";
       ST q = templates.getInstanceOf("Quit");
       q.add("nick", user.getNick()).add("user", user.getName()).add("host", host).add("nachricht", message).add("clienthost", user.getAddress());
       for(Map.Entry<String,List<User>> entry : channelUserMap.entrySet()){
            for(User u: entry.getValue()){
                if(u.equals(user))  channelUserMap.get(entry.getKey()).remove(user);
                break;
            }
       }
        user.getIRCSlave().interrupt();
        users.remove(user);
       for (User u : users) {
           u.sendMessage(q.render());
       }
    }



}
