package project03;

import java.io.IOException;
import java.net.ServerSocket;

public class ServidorEcoMultihilo {
    public static final int PUERTO = 9999;
    public static final int MAXIMO = 3;
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            int conexiones = 0;
            // 2. Mientras no se llegue a MAXIMO: acepta una conexión, cuéntala
            while (conexiones < MAXIMO) {
                // y arranca un hilo nuevo que ejecute un ServicioEco sobre ella
                new Thread(new ServicioEco(serverSocket.accept(), conexiones++)).start();
            }
            // 3. Al salir del bucle, avisa de que se ha alcanzado el máximo
            System.out.println("Se ha alcanzado el máximo de conexiones");
            Thread.sleep(30000);
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
        // 4. Al salir se cierra el ServerSocket: no entran clientes nuevos,
        // y los hilos de servicio siguen hasta que sus clientes cierran
    }
}

