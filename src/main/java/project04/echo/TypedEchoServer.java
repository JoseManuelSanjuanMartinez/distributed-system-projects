package project04.echo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Part 1 — Typed echo server (YOUR TASK).
 *
 * Protocol (one round per connection):
 *   Request:  int n | double x | UTF s
 *   Response: int n+1 | double 2*x | UTF s in upper case
 *
 * The reply transforms each value so that you can SEE that the server
 * really parsed three typed values (it is not echoing raw bytes).
 */
public class TypedEchoServer {

    public static final int PORT = 9944;

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TypedEchoServer listening on port " + PORT);
            while (true) {
                // accept() blocks until a client connects (same as in Práctica 2)
                try (Socket socket = serverSocket.accept();
                     DataInputStream dis = new DataInputStream(socket.getInputStream());
                     DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                    // TODO (1): read the three values IN THE ORDER the
                    //           client writes them (readInt, readDouble,
                    //           readUTF) and print them.
                    int n = dis.readInt();
                    double x = dis.readDouble();
                    String s = dis.readUTF();

                    // TODO (2): write the transformed reply (n+1, 2*x, upper
                    //           case) and FLUSH the output stream. Not needed on
                    //           an unbuffered stream like this one, but the habit
                    //           that saves you the day you add a BufferedOutputStream.
                    dos.writeInt(n + 1);
                    dos.writeDouble(2 * x);
                    dos.writeUTF(s.toUpperCase());
                    dos.flush();

                    // TODO (3): try to read one more int. The client has
                    //           closed by now. Which exception do you get?
                    //           Catch it and print a message. Compare with
                    //           what readLine() returns on a closed TEXT
                    //           stream (Práctica 2).
                    int noneExistentInteger = dis.readInt();
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
        }
    }
}
