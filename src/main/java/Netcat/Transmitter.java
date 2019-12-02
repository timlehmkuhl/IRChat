package Netcat;
public class Transmitter<A> implements Actor<A> {

	private TCPSocket TCPSocket;
	
	public Transmitter(TCPSocket socket) {
		this.TCPSocket = socket;
	}



	@Override
    public void tell(String message, Actor<A> sender)  {
        TCPSocket.send(message);
	}

	@Override
	public void shutdown() {
		TCPSocket.send("\u0004");
	}

	
	
	

}
