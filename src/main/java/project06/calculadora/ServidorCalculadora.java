package project06.calculadora;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Práctica 6 · El servidor: un ServerSocket, un hilo por conexión (práctica 3)
 * y una conexión por llamada. El sirviente es uno para todos los clientes,
 * así que todos comparten la memoria de acumular.
 *
 * Uso: java -cp clases calculadora.ServidorCalculadora [--pierde-respuestas]
 */
public class ServidorCalculadora {

    public static void main(String[] args) throws IOException {
        boolean pierde = args.length > 0 && args[0].equals("--pierde-respuestas");
        CalculadoraSkeleton skeleton = new CalculadoraSkeleton(new CalculadoraLocal(), pierde);

        try (ServerSocket servidor = new ServerSocket(Protocolo.PUERTO)) {
            System.out.println("Calculadora escuchando en el puerto " + Protocolo.PUERTO
                    + (pierde ? " (pierde una respuesta de cada dos)" : ""));
            while (true) {
                Socket socket = servidor.accept();
                new Thread(() -> {
                    try {
                        skeleton.atender(socket);
                    } catch (IOException e) {
                        System.out.println("petición abandonada: " + e);
                    }
                }).start();
            }
        }
    }
}
