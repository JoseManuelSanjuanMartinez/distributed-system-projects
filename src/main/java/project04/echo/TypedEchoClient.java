package project04.echo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Part 1 — Typed echo client.
 * Sends int | double | UTF and prints the transformed reply.
 */
public class TypedEchoClient {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", TypedEchoServer.PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            dos.writeInt(41);
            dos.writeDouble(1.5);
            dos.writeUTF("distributed systems");
            dos.flush();

            int n = dis.readInt();
            double x = dis.readDouble();
            String s = dis.readUTF();
            System.out.println("reply: n=" + n + "  x=" + x + "  s=\"" + s + "\"");
        }
        // Closing the socket is what makes the server see EOFException.
    }
}
