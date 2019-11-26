package Netcat;

public class Netcat {

    public static void main(String[] args) throws Exception {


        if (args.length != 2) {
            System.err.println("Zu wenig Parameter");
            return;
        }


        if (!args[0].equals("-l")) {
            //Client
            //$ java Netcat ip 5555
            String host = args[0];
            System.out.println("Client started!");

            int port = Integer.parseInt(args[1]);

            Transceiver transceiver = new Transceiver(port,host);
            ReaderPrinter readerPrinter = new ReaderPrinter(transceiver);
            readerPrinter.start();


        }

        if(args[0].equals("-l")){
            //Server
            //$ java Netcat -l 2222
            int port = Integer.parseInt(args[1]);
            System.out.println("Server started!");

            ReaderPrinter  readerPrinter= new ReaderPrinter();
            Transceiver transceiver= new Transceiver(port,readerPrinter);
            transceiver.start(true);


        }
    }
}
