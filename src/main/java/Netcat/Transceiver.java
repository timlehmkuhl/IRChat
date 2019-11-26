package Netcat;
public class Transceiver<A> implements Actor<A> {

	private Actor transmitter;
	private Receiver receiver;
	private TCPSocket TCPSocket;
	private boolean server;


	/**
	 * Konstruktor fuer Server
	 * @param port
	 * @param readerPrinter
	 */
	public Transceiver(int port, ReaderPrinter readerPrinter) {
        TCPSocket = new TCPSocket(port);
		receiver = new Receiver(TCPSocket, readerPrinter,this, server);
		transmitter = new Transmitter(TCPSocket);
	}

	public void start(boolean server){
		receiver.start();
		this.server = server;
		receiver.setServer(true);
	}


	/**
	 * Konstruktor fuer Client
	 * @param port
	 * @param host
	 */
	public Transceiver(int port, String host)  {
        TCPSocket = new TCPSocket(port, host);
		transmitter = new Transmitter(TCPSocket);
	}


	@Override
	public void tell(String message, Actor sender) throws Exception {
		transmitter.tell(message, sender);

		if(receiver == null) {
			receiver = new Receiver(TCPSocket,sender,this, server);
			receiver.start();
		}
	}

	@Override
	public void shutdown() {

	}


}
