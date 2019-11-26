package IRC;

import java.io.IOException;

public class User {
    private String nick;
    private String name;
    private String address;
    private boolean register;
    private ClientManager clientManagerThread;


    public User(String nick, String name, String address, ClientManager clientManagerThread, boolean register) {
        this.nick = nick;
        this.name = name;
        this.address = address;
        this.clientManagerThread = clientManagerThread;
        this.register = register;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }


    @Override
    public boolean equals(Object obj) {
        User temp = (User) obj;
        return temp.getAddress().equals(this.address);
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }



    public ClientManager getClientManagerThread() {
        return clientManagerThread;
    }



    public void sendMessage(String message) throws IOException {
        clientManagerThread.tell(message, null);
    }
}