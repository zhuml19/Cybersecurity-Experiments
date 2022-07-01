import java.net.Socket;
import java.security.PrivateKey;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.security.MessageDigest;

// Server should be acted as B

public class Server extends Thread {
    ServerSocket serverSocket;
    String pw = "Ep1phanyFillTo16";

    public Server(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10000);
    }

    public void run() {

        while (true) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                RSA rsa = new RSA();
                AES aes = new AES();
                DES des = new DES();
                Socket server = serverSocket.accept();
                System.out.println("Accepted connection from " + server.getRemoteSocketAddress());
                ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(server.getInputStream());
                // Receive 1: Identity and E_0(pw, pk_A)
                String identity = (String) in.readObject();
                System.out.println("Received unverified identity: " + identity);
                String pw_cipher = (String) in.readObject();
                // decrypt pw_cipher to get pk_A
                System.out.println("DEBUG pw_cipher: " + pw_cipher);
                aes.setKey(pw.getBytes());
                String pk_A = new String(aes.decrypt(pw_cipher));
                // Generate K_s
                PrivateKey K_s = rsa.genPrivateKey();
                rsa.setPublicKey(pk_A.getBytes());
                // Send 1: E_0(pw, E(pk_A, K_s))
                out.writeObject(aes.encrypt(rsa.encrypt(K_s.getEncoded().toString()).toString()).toString());
                out.flush();
                // Receive 2: E_1(K_s, N_A)
                String N_A_cipher = (String) in.readObject();
                // decrypt N_A_cipher to get N_A    
                des.setKey(K_s.getEncoded());
                String N_A = new String(des.decrypt(N_A_cipher));
                // Create N_B
                String N_B = rsa.genRandomString(16);
                String Concat = N_A + N_B;
                // Send 2: E_1(K_s, Concat)
                out.writeObject(des.encrypt(Concat).toString());
                out.flush();
                // Receive 3: E_1(K_s, N_2)
                String N_2_cipher = (String) in.readObject();
                // decrypt N_2_cipher to get N_2
                if (N_B == des.decrypt(N_2_cipher).toString()) {
                    System.out.println("Server Authentication succeeded, Client identity: " + identity);
                } else {
                    System.out.println("Server Authentication failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Thread t = new Server(7654);
        t.start();
    }
}