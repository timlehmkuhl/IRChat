package Netcat;
public class Printer<A> implements Actor<A>{

    private String strOut;

    public Printer() {
    }


    /**
     * Gibt eingehende Nachrichten auf der Console aus
     * @param message
     * @param sender
     */
    @Override
    public void tell(String message, Actor sender) {
        this.strOut = message;
        System.out.println(message);
    }

    @Override
    public void shutdown() {

    }

    public String getStrOut() {
        return strOut;
    }
}
