package project07.orb1;

import java.io.IOException;
import java.net.Socket;

/**
 * Práctica 7 · Parte 1 · Un cliente que se conecta y no dice nada (pregunta P2).
 *
 * Abre una conexión con el servidor de la parte 1 y la mantiene abierta, sin
 * enviar ni un byte, durante los segundos que se le indiquen (15 si no se le
 * indica nada). Después la cierra.
 *
 * Uso: ClienteMudo [segundos]
 */
public class ClienteMudo {

    public static void main(String[] args)
            throws IOException, InterruptedException {
        int segundos = args.length > 0 ? Integer.parseInt(args[0]) : 15;
        try (Socket socket = new Socket("localhost", Protocolo.PUERTO)) {
            System.out.println("Conectado al puerto " + socket.getPort() + "; "
                    + segundos + " s sin enviar nada...");
            Thread.sleep(segundos * 1000L);
        }
        System.out.println("Conexión cerrada.");
    }
}
