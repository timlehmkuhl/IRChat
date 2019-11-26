package Netcat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class TCPSocket {

    private Socket socket;
    private String ip;
    PrintWriter printWriter ;
    BufferedReader bufferedReader;

    /**
     * Konstruktor fuer Client
     * @param ip
     * @param port
     * @throws SocketException
     */
    public TCPSocket(int port, String ip)  {
        this.ip = ip;

        try {
            this.socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            printWriter= new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Konstruktor fuer Server
     * @param port
     * @throws SocketException
     */
    public TCPSocket(int port) {

        try {
            socket = new ServerSocket(port).accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            printWriter= new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ein String wird vor dem Senden in ein Byte-Array gewandelt und
     * in ein DatagramPacket gelegt, welches dem Socket uebergeben wird
     * @param message
     * @throws IOException
     */
    public void send(String message)  {
        try {
            //  socket.getOutputStream().write(message.getBytes());
            printWriter.write(message + "\n");
            printWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * In der send- und receive-Methode
     * Ein Byte-Array aus dem empfangenden DatagramPacket wird in ein String gewandelt
     * @param maxBytes
     * @return String
     * @throws IOException
     */
    public String receive(int maxBytes) throws IOException {
      /*  BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        char[] buffer = new char[maxBytes];
        int length = 0;
        try {
         length = bufferedReader.read(buffer, 0, buffer.length);
         return new String(buffer, 0, length);
        } catch (Exception e) {
            System.err.println("Client beendet!");
            System.exit(0);
            return "";
        }
*/
        String ret = "";
        ret = bufferedReader.readLine();

        return  ret;
    }

    public void shutdown() {
    }

    public String getHostClient(){
        return socket.getInetAddress().getHostAddress();
    }

    public String getHostServer(){
        return socket.getLocalAddress().getHostAddress();
    }


}
