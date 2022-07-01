import java.net.Socket;
import java.net.ServerSocket;

public class Server extends Thread {
    ServerSocket serverSocket;

    public Server(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(2000);
    }

    public static void main(String[] args) throws Exception {
        Thread t = new Server(7654);
        t.start();
    }
}