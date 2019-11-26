package Netcat;
public class Receiver extends Thread{

	private TCPSocket socket;
	private Actor readprinter;
	private Actor transceiver;
	private String nachricht;
    private static final int BUFSIZE = 10000;
    private boolean server;


	public Receiver(TCPSocket socket, Actor readprinter, Actor transceiver, boolean server){
		this.socket = socket;
		this.readprinter = readprinter;
		this.transceiver = transceiver;
		this.server = server;
	}

    public void setServer(boolean server) {
        this.server = server;
    }

    /**
     * Nachrichten werden im Thread empfangen
     */
	public void run() {
        while (true){
            try {
               // System.out.println(server);

                this.nachricht = socket.receive(BUFSIZE);
                if(server == true){
                    System.err.println("server");
                    if(nachricht.startsWith("START")) transceiver.tell("Chat start successfull", transceiver);
                }
                this.readprinter.tell(nachricht, transceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(nachricht.equals("\u0004")){
                readprinter.shutdown();
                break;
            }
        }
	}

}
