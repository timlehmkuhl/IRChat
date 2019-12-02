package IRC;

import java.io.IOException;

public class User {
    private String nick;
    private String name;
    private String address;
    private boolean register;
    private IRCSlave IRCSlave;


    public User(String nick, String name, String address, IRCSlave IRCSlave, boolean register) {
        this.nick = nick;
        this.name = name;
        this.address = address;
        this.IRCSlave = IRCSlave;
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


    public boolean isRegistered() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }


    public void sendMessage(String message) throws IOException {
        IRCSlave.tell(message, null);
    }

    @Override
    public boolean equals(Object obj) {
        User temp = (User) obj;
        return temp.getAddress().equals(this.address);
    }

    public Thread getIRCSlave() {
        return this.IRCSlave;
    }
}
