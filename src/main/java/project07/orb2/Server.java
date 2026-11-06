package project07.orb2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Práctica 7 · Parte 2 · El servidor de varias agendas remotas.
 *
 * Monohilo, una petición por conexión, como en la parte 1. Ahora guarda muchos
 * objetos Agenda en la ObjsTable, una tabla hash de tuplas <objId, Agenda>, y
 * cada petición «escribir» o «leer» trae el objId del objeto que hay que usar.
 * Con un objId que no está en la tabla, lee el resto de la petición y responde
 * false.
 */
public class Server {

    private final Map<Integer, Agenda> objsTable;
    private int nextObjId;
    private int conexiones = 0;

    public Server() {
        objsTable = new Hashtable<>(2);
        nextObjId = 1;
    }

    public static void main(String[] args) throws IOException {
        new Server().servir();
    }

    private void servir() throws IOException {
        try (ServerSocket servidor = new ServerSocket(Protocolo.PUERTO)) {
            System.out.println("Servidor de la parte 2 escuchando en el puerto "
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

    /** Una petición: la lee entera, elige el objeto por su objId y responde. */
    private void atender(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        int opcode = dis.readInt();
        switch (opcode) {
            case Protocolo.CREAR -> {
                Agenda newObj = new Agenda();
                objsTable.put(nextObjId, newObj);

                dos.writeBoolean(true);
                dos.writeInt(nextObjId++); // Send and increment for the next object ID
            }
            case Protocolo.ESCRIBIR -> {
                try {
                    // Unmarshalling --> [
                    int objId = dis.readInt();
                    String key = dis.readUTF();
                    int value = dis.readInt();
                    // ]

                    if (!objsTable.containsKey(objId)) {
                        throw new NoSuchElementException(String.format(
                                "id de objeto no encontrado: escribir(%d, %d, %s, %d) -> false",
                                opcode,
                                objId,
                                key,
                                value
                        ));
                    }
                    Agenda obj = objsTable.get(objId);
                    obj.write(key, value);

                    dos.writeBoolean(true); // Response
                } catch (NoSuchElementException e) {
                    log(e.getMessage());
                    dos.writeBoolean(false);
                } catch (NullPointerException e) {
                    log("el cliente cerró la conexión de forma inesperada: escribir");
                }
            }
            case Protocolo.LEER -> {
                try {
                    // Unmarshalling --> [
                    int objId = dis.readInt();
                    String key = dis.readUTF();
                    // ]

                    if (!objsTable.containsKey(objId)) {
                        throw new NoSuchElementException(String.format(
                                "id de objeto no encontrado: leer(%d, %d, %s) -> false",
                                opcode,
                                objId,
                                key
                        ));
                    }
                    Agenda obj = objsTable.get(objId);
                    int value = obj.read(key);

                    // Response --> [
                    dos.writeBoolean(true);
                    dos.writeInt(value);
                    // ]
                } catch (NoSuchElementException e) {
                    log(e.getMessage());
                    dos.writeBoolean(false);
                } catch (NullPointerException e) {
                    log("el cliente cerró la conexión de forma inesperada: escribir(...)");
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
