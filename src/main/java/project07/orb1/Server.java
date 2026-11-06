package project07.orb1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * Práctica 7 · Parte 1 · El servidor de la Agenda remota.
 *
 * Monohilo: atiende una conexión cada vez, y una petición por conexión. Lee la
 * petición entera, invoca el único objeto Agenda y responde siempre con un
 * boolean (true si ha podido hacer la operación); en «leer», si es true, envía
 * detrás el valor.
 * El código que desempaqueta la petición y el switch que elige la operación
 * hacen de skeleton y de despachador; la Agenda es el sirviente. El servidor
 * anota cada conexión aceptada y cada operación, con el número de la conexión.
 */
public class Server {

    /** El único objeto del servidor: se crea con la primera petición «crear». */
    private Agenda agenda = null;

    private int conexiones = 0;

    public static void main(String[] args) throws IOException {
        new Server().servir();
    }

    private void servir() throws IOException {
        try (ServerSocket servidor = new ServerSocket(Protocolo.PUERTO)) {
            System.out.println("Servidor de la parte 1 escuchando en el puerto "
                    + Protocolo.PUERTO);
            while (true) {
                try (Socket socket = servidor.accept()) {
                    conexiones++;
                    log("conexión aceptada");
                    atender(socket);
                } catch (IOException e) {
                    log("petición abandonada: " + e);
                }
            }
        }
    }

    /** Una petición: la lee entera, la ejecuta y responde. */
    private void atender(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        int opcode = dis.readInt();
        switch (opcode) {
            case Protocolo.CREAR -> {
                // la agenda se crea sólo la primera vez
                if (agenda == null) {
                    agenda = new Agenda();
                }
                dos.writeBoolean(true);
                log("crear -> true");
            }
            case Protocolo.ESCRIBIR -> {
                String key = dis.readUTF();
                int value = dis.readInt();
                boolean opStatus = false;

                if (agenda != null) {
                    agenda.write(key, value);
                    opStatus = true;
                }

                dos.writeBoolean(opStatus);
                log(String.format("escribir(%s, %d) -> %b",key, value, opStatus));
            }
            case Protocolo.LEER -> {
                String key = dis.readUTF();
                boolean opStatus = false;
                int value = -1;


                try {
                    if (agenda != null) {
                        value = agenda.read(key);
                        opStatus = true;
                    }
                } catch (NoSuchElementException e) {
                    opStatus = false;
                }

                dos.writeBoolean(opStatus);
                if (opStatus) {
                    dos.writeInt(value);
                    log(String.format("leer(%s) -> %b, %d",key, opStatus, value));
                } else {
                    log(String.format("leer(%s) -> %b",key, opStatus));
                }
            }
            default -> {
                dos.writeBoolean(false);
                log("código de operación desconocido: " + opcode + " -> false");
            }
        }
        dos.flush();
    }

    private void log(String texto) {
        System.out.println("[" + conexiones + "] " + texto);
    }
}
