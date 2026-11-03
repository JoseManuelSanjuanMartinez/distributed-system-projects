package project06.calculadora;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Práctica 6 · El stub del cliente (el proxy).
 *
 * Implementa la misma interfaz que CalculadoraLocal, así que el programa
 * cliente lo usa sin saber que la operación se ejecuta en otra máquina.
 * Es la única clase del cliente que conoce el protocolo: empaqueta los
 * argumentos (marshalling), envía la petición, espera, desempaqueta la
 * respuesta y devuelve un int normal, o lanza la excepción que toque.
 *
 * Una conexión por llamada: cada método abre su socket y lo cierra.
 */
public class CalculadoraProxy implements Calculadora {

    /** Plazo de espera de una respuesta (setSoTimeout, práctica 4). */
    public static final int PLAZO_MS = 2000;

    private final String maquina;
    private final int puerto;
    private final int intentos;                       // parte 3: 1 = sin reintentos

    /** Parte 4: identificadores de llamada únicos para este proxy. */
    private final AtomicLong siguienteId =
            new AtomicLong(ThreadLocalRandom.current().nextLong() & 0x7fff_ffff_0000_0000L);

    public CalculadoraProxy(String maquina, int puerto) {
        this(maquina, puerto, 1);
    }

    public CalculadoraProxy(String maquina, int puerto, int intentos) {
        this.maquina = maquina;
        this.puerto = puerto;
        this.intentos = intentos;
    }

    @Override
    public int sumar(int a, int b) {
        return invocar(Protocolo.OP_SUMAR, a, b);
    }

    @Override
    public int restar(int a, int b) {
        return invocar(Protocolo.OP_RESTAR, a, b);
    }

    @Override
    public int multiplicar(int a, int b) {
        return invocar(Protocolo.OP_MULTIPLICAR, a, b);
    }

    @Override
    public int dividir(int a, int b) {
        return invocar(Protocolo.OP_DIVIDIR, a, b);
    }

    @Override
    public int acumular(int x) {
        return invocar(Protocolo.OP_ACUMULAR, x);
    }

    /**
     * Una llamada remota completa, con sus reintentos (parte 3). Si la
     * conexión falla o la respuesta no llega, lo vuelve a intentar hasta
     * agotar los intentos: el proxy no puede saber si el servidor llegó a
     * ejecutar la operación.
     */
    private int invocar(int opcode, int... args) {
        long id = siguienteId.incrementAndGet();      // el mismo en todos los reintentos
        IOException ultimo = null;
        for (int intento = 1; intento <= intentos; intento++) {
            try {
                return unaVez(id, opcode, args);
            } catch (IOException e) {
                ultimo = e;                           // conexión rechazada, cortada o sin respuesta
            }
        }
        throw new LlamadaRemotaException("la llamada a " + maquina + ":" + puerto
                + " ha fallado tras " + intentos + " intento(s): " + ultimo, ultimo);
    }

    private int unaVez(long id, int opcode, int... args) throws IOException {
        try (Socket socket = new Socket(maquina, puerto);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            socket.setSoTimeout(PLAZO_MS);

            // 1. Marshalling de la petición
            if (Protocolo.CON_ID) {
                dos.writeLong(id);
            }
            dos.writeInt(opcode);
            for (int arg : args) {
                dos.writeInt(arg);
            }
            dos.flush();

            // 2. Unmarshalling de la respuesta
            byte estado = dis.readByte();
            if (estado == Protocolo.OK) {
                return dis.readInt();
            } else {
                String err = dis.readUTF();
                throw new ArithmeticException(err);
            }

        }
    }
}
