package project02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteEcoTCP {
    public static final String MAQUINA = "localhost";
    public static final int PUERTO = 9999;
    public static void main(String[] args) {
        try (
                // 1. Un socket conectado a MAQUINA:PUERTO
                Socket socket = new Socket(MAQUINA, PUERTO);
                // 2. Un PrintWriter sobre el flujo de salida del socket, con autovaciado
                PrintWriter outputToServer = new PrintWriter(socket.getOutputStream(), true);
                // 3. Un BufferedReader sobre el flujo de entrada del socket
                BufferedReader outputFromServer = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                // 4. Un BufferedReader sobre System.in, para leer del teclado
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(System.in)
                );
        ) {
            // 5. Lee del teclado hasta el fin de fichero (readLine() == null)...
            String line = input.readLine();
            while (line != null) {
                // 6. ...enviando cada línea al servidor e imprimiendo el eco que llega
                outputToServer.println(line);
                System.out.println(outputFromServer.readLine());
                line = input.readLine();
            };
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        // al cerrarse el socket, el servidor ve el final de la conversación
    }
}