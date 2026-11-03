package project03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ServicioEco implements Runnable {
    private final Socket socket;
    private final int id;

    public ServicioEco(Socket socket, int id) {
        // 1. Guarda el socket del cliente y el número de conexión
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void run() {
        try (
                // 2. El socket, para que se cierre al terminar
                socket;
                // 3. Un BufferedReader sobre el flujo de entrada del socket
                BufferedReader outputFromSocket = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                // 4. Un PrintWriter sobre el flujo de salida, con autovaciado
                PrintWriter outputToSocket = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // 5. Avisa en la consola del servidor de que este cliente
            // ha entrado, con su id
            System.out.printf("Nuevo cliente conectado / id: %d%n", id);
            // 6. Hasta el fin de fichero: lee una línea,
            // imprímela con su id y devuélvela
            String line = outputFromSocket.readLine();
            while (line != null) {
                System.out.printf("Cliente (%d): %s%n", id, line);
                outputToSocket.println(line);
                line = outputFromSocket.readLine();
            }
            // 7. Al salir del bucle, avisa de que el cliente ha cerrado
            System.out.printf("Cliente %d ha cerrado la conexión%n", id);
        } catch (IOException e) {
            System.out.println("[" + id + "] " + e.getMessage());
        }
    }
}
