package project06.calculadora;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Práctica 6 · El skeleton del servidor.
 *
 * Atiende una petición por conexión: lee el opcode, desempaqueta los
 * argumentos, llama a la implementación de verdad (el «sirviente») y
 * empaqueta la respuesta. Si la operación lanza una excepción, no la envía:
 * envía un estado ERROR y el mensaje, y el proxy la reconstruye.
 */
public class CalculadoraSkeleton {

    private final Calculadora sirviente;
    private final boolean pierdeRespuestas;           // parte 3: --pierde-respuestas
    private final AtomicLong peticiones = new AtomicLong();

    /** Parte 4: la respuesta ya calculada para cada llamada, por su identificador (se guarda antes de enviarla). */
    private final Map<Long, Respuesta> hechas = new ConcurrentHashMap<>();

    public CalculadoraSkeleton(Calculadora sirviente, boolean pierdeRespuestas) {
        this.sirviente = sirviente;
        this.pierdeRespuestas = pierdeRespuestas;
    }

    /** Lo que se envía de vuelta: un estado y, según el estado, un int o un mensaje. */
    private record Respuesta(byte estado, int resultado, String mensaje) { }

    public void atender(Socket socket) throws IOException {
        try (socket;
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            long id = Protocolo.CON_ID ? dis.readLong() : 0;
            int opcode = dis.readInt();

            // Parte 4 (opcional), TODO (6): con CON_ID, si la llamada id ya está en
            // «hechas», no la ejecutes otra vez: lee sus argumentos sin usarlos
            // (consumirArgumentos) y reenvía la respuesta guardada. Si no está,
            // ejecútala y guarda su respuesta en «hechas».
            Respuesta r;
            if (hechas.containsKey(id)) {
                consumirArgumentos(dis, opcode);
                r = hechas.get(id);
            } else {
                r = ejecutar(dis, opcode);
                hechas.put(id, r);
            }

            long n = peticiones.incrementAndGet();
            if (pierdeRespuestas && n % 2 == 1) {
                return;                               // ejecutada, pero la respuesta «se pierde»
            }

            dos.writeByte(r.estado());
            if (r.estado() == Protocolo.OK) {
                dos.writeInt(r.resultado());
            } else {
                dos.writeUTF(r.mensaje);
            }
            dos.flush();
        }
    }

    private Respuesta ejecutar(DataInputStream dis, int opcode) throws IOException {
        try {
            int resultado = switch (opcode) {
                case Protocolo.OP_SUMAR -> sirviente.sumar(dis.readInt(), dis.readInt());
                case Protocolo.OP_RESTAR -> sirviente.restar(dis.readInt(), dis.readInt());
                case Protocolo.OP_MULTIPLICAR -> sirviente.multiplicar(dis.readInt(), dis.readInt());
                case Protocolo.OP_DIVIDIR -> sirviente.dividir(dis.readInt(), dis.readInt());
                case Protocolo.OP_ACUMULAR -> sirviente.acumular(dis.readInt());
                default -> throw new IOException("opcode desconocido: " + opcode);
            };
            return new Respuesta(Protocolo.OK, resultado, null);
        } catch (ArithmeticException e) {
            return new Respuesta(Protocolo.ERROR, 0, e.getMessage());
        }
    }

    private static void consumirArgumentos(DataInputStream dis, int opcode) throws IOException {
        int n = (opcode == Protocolo.OP_ACUMULAR) ? 1 : 2;
        for (int i = 0; i < n; i++) {
            dis.readInt();
        }
    }
}
