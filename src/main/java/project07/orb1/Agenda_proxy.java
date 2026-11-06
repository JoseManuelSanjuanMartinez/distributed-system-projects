package project07.orb1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Práctica 7 · Parte 1 · El proxy de la Agenda remota, en el cliente.
 *
 * Implementa IRepository, así que el programa cliente lo usa como usaría una
 * Agenda local. Es la única clase del cliente que sabe dónde está el objeto
 * (MAQUINA y Protocolo.PUERTO) y cómo se habla con él: cada método abre una
 * conexión TCP, escribe la petición, lee la respuesta y cierra la conexión.
 */
public class Agenda_proxy implements IRepository {

    private static final String MAQUINA = "localhost";

    /** Pide al servidor que cree la Agenda (si todavía no existe). */
    public Agenda_proxy() {
        try (Socket socket = new Socket(MAQUINA, Protocolo.PUERTO);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            // petición: 1
            dos.writeInt(Protocolo.CREAR);
            dos.flush();

            // respuesta: boolean
            if (!dis.readBoolean()) {
                throw new LlamadaRemotaException(
                        "el servidor no ha podido crear la agenda");
            }
        } catch (IOException e) {
            throw new LlamadaRemotaException("crear: " + e, e);
        }
    }

    @Override
    public void write(String key, int value) {
        try (Socket socket = new Socket(MAQUINA, Protocolo.PUERTO);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            dos.writeInt(Protocolo.ESCRIBIR);
            dos.writeUTF(key);
            dos.writeInt(value);
            dos.flush();

            if (!dis.readBoolean()) {
                throw new LlamadaRemotaException("el servidor no ha podido guardar el contacto");
            }
        } catch (IOException e) {
            throw new LlamadaRemotaException("escribir: " + e, e);
        }
    }

    @Override
    public int read(String key) {
        try (Socket socket = new Socket(MAQUINA, Protocolo.PUERTO);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            dos.writeInt(Protocolo.LEER);
            dos.writeUTF(key);
            dos.flush();

            if (!dis.readBoolean()) {
                throw new LlamadaRemotaException("no existe el contacto en el servidor");
            }

            return dis.readInt();
        } catch (IOException e) {
            throw new LlamadaRemotaException("leer: " + e, e);
        }
    }
}
