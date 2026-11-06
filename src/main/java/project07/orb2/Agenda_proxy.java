package project07.orb2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Práctica 7 · Parte 2 · El proxy de una de las agendas remotas.
 *
 * Cada proxy representa un objeto Agenda distinto del servidor. Al construirse
 * pide al servidor que cree el objeto y guarda el objId que éste le asigna;
 * después envía ese objId en cada petición, para que el servidor sepa sobre qué
 * objeto ejecutarla. Como en la parte 1, una conexión TCP por llamada.
 */
public class Agenda_proxy implements IRepository {

    private static final String MAQUINA = "localhost";
    private final int objId;

    /** Pide al servidor que cree un objeto Agenda nuevo y guarda su objId. */
    public Agenda_proxy() {
        try (Socket socket = new Socket(MAQUINA, Protocolo.PUERTO);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream()))
        {
            dos.writeInt(Protocolo.CREAR);
            dos.flush();

            if (!dis.readBoolean()) {
                throw new LlamadaRemotaException(
                        "el servidor no ha podido crear la agenda");
            }

            objId = dis.readInt();

        } catch (IOException e) {
            throw new LlamadaRemotaException("crear: " + e, e);
        }
    }

    @Override
    public void write(String key, int value) {
        try (Socket socket = new Socket(MAQUINA, Protocolo.PUERTO);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream()))
        {
            dos.writeInt(Protocolo.ESCRIBIR);
            dos.writeInt(objId);
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
             DataInputStream dis = new DataInputStream(socket.getInputStream()))
        {
            dos.writeInt(Protocolo.LEER);
            dos.writeInt(objId);
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
