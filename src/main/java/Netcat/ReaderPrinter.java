package Netcat;
public class ReaderPrinter<A> implements Actor<A>{

	 private Reader reader;
	 private Printer printer;


	/**
	 * Konstruktor fuer Server
	 */
	public ReaderPrinter() {
		printer = new Printer();
	}


	/**
	 * Konstruktor fuer Client
	 * @param transceiver
	 */
	public ReaderPrinter(Transceiver transceiver) {
		reader = new Reader(transceiver,this);
		printer = new Printer();
		//reader.start();
	}

	public void start(){
		reader.start();
	}

	@Override
	public void tell(String message, Actor sender) {
		printer.tell(message, sender);

		if(reader == null) {
			reader = new Reader(sender,this);
			reader.start();
		}
	}



	@Override
	public void shutdown() {

	}

	public Reader getReader() {
		return reader;
	}

	public Printer getPrinter() {
		return printer;
	}
}
