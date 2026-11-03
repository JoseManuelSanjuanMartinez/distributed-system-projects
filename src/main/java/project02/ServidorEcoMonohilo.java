package project02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorEcoMonohilo {
    public static final int PUERTO = 9999;
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            try (
                    // 2. Espera (bloqueado en accept) a que entre un cliente
                    Socket socket = serverSocket.accept();
                    // 3. Un BufferedReader sobre el flujo de entrada del socket
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream())
                    );
                    // 4. Un PrintWriter sobre el flujo de salida, con autovaciado
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            ) {
                // 5. Lee líneas del socket hasta que el cliente cierre (null)...
                String line = bufferedReader.readLine();
                while (line != null) {
                    // 6. ...devolviendo cada una tal cual
                    output.println(line);
                    line = bufferedReader.readLine();
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}