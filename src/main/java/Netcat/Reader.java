package Netcat;
import java.util.Scanner;

public class Reader extends Thread{

	private Actor transceiver;
    private Actor readerprinter;
    private String strIn = "";

	
	public Reader(Actor transceiver, Actor readerprinter){
		this.transceiver = transceiver; 
		this.readerprinter = readerprinter;
	}

    /**
     * Liest Nachrichten in einem Thread ein
     */
    @Override
    public void run() {
        Scanner in = new Scanner(System.in);
        if(strIn != "") {
            try {
                transceiver.tell(strIn, readerprinter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        while(in.hasNext()) {
            try {
                transceiver.tell(in.nextLine(), readerprinter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        in.close();
        try {
            transceiver.tell("\u0004", readerprinter);
            readerprinter.shutdown();
            transceiver.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setStrIn(String strIn) {
        this.strIn = strIn;
    }
}
