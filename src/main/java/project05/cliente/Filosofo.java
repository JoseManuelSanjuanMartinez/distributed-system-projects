package project05.cliente;

import project05.comun.Protocolo;
import project05.comun.Registro;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.random.RandomGenerator;

/**
 * Un filósofo: un hilo que necesita un ordenador y un teclado a la vez para conectarse al
 * teatro, y que compra entradas de diez en diez hasta que tiene las que quiere o se agotan.
 *
 * El ciclo de vida está en run(): coger los dos recursos (o soltar y esperar un tiempo al
 * azar), visitar el teatro, soltar los recursos, y vuelta a empezar.
 */
public class Filosofo implements Runnable {

    public static final int ESPERA_CERROJO_MS = 500;   // cuánto insiste tryLock por un recurso
    public static final int ESPERA_MINIMA_MS = 100;    // la espera al azar está entre estos dos
    public static final int ESPERA_MAXIMA_MS = 500;
    public static final int FALLOS_CONEXION_MAXIMOS = 5;

    private final int numero;
    private final Recursos recursos;
    private final String maquina;
    private final int puerto;
    private final Random azar = new Random();
    private final List<Integer> entradas = new ArrayList<>();

    private Lock ordenador;          // los recursos que tiene cogidos ahora mismo, o null
    private Lock teclado;
    private boolean agotadas = false;
    private int fallosConexion = 0;

    // Thread-owned pseudorandom number generator to reduce contention between threads if Math.random() is used.
    private RandomGenerator randomGenerator;

    public Filosofo(int numero, Recursos recursos, String maquina, int puerto) {
        this.numero = numero;
        this.recursos = recursos;
        this.maquina = maquina;
        this.puerto = puerto;
        this.randomGenerator = new Random();
    }

    @Override
    public void run() {
        try {
            while (!agotadas && entradas.size() < Protocolo.ENTRADAS_DESEADAS
                    && fallosConexion < FALLOS_CONEXION_MAXIMOS) {
                if (!cogerOrdenadorYTeclado()) {
                    esperaAlAzar();               // no tiene nada en las manos: espera y reintenta
                    continue;
                }
                boolean atendido;
                try {
                    atendido = visitarElTeatro();
                } finally {
                    soltarOrdenadorYTeclado();    // pase lo que pase, los demás los necesitan
                }
                if (!atendido) {
                    esperaAlAzar();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        di("he podido comprar " + entradas.size() + " entradas: " + entradas);
    }

    /** Las entradas que ha conseguido, para el recuento final. */
    public List<Integer> entradas() {
        return entradas;
    }

    /**
     * Un intento de coger un ordenador y un teclado al azar, y en un orden al azar.
     *
     * @return true si tiene los dos; false si ha soltado lo que tenía y no tiene ninguno
     */
    private boolean cogerOrdenadorYTeclado() throws InterruptedException {
        // TAREA 5. Elige al azar un ordenador y un teclado de `recursos`, y al azar cuál
        // de los dos coger primero. Llama a cogerDos(primero, segundo). Si devuelve true,
        // guarda los dos cerrojos en los atributos `ordenador` y `teclado` (para que
        // soltarOrdenadorYTeclado() sepa qué soltar) y devuelve true; si no, devuelve false.
        int randComputerId = randomGenerator.nextInt(0, recursos.numOrdenadores());
        ordenador = recursos.ordenador(randComputerId);
        int randKeyboardId = randomGenerator.nextInt(0, recursos.numTeclados());
        teclado = recursos.teclado(randKeyboardId);
        // Selección aleatoria de cerrojos
        // 0: se bloquea primero el ordenador
        // 1: se bloquea primero el teclado
        int lockAcquirementOrder = randomGenerator.nextInt(0, 2);
        Lock firstLock = lockAcquirementOrder == 0 ? ordenador : teclado;
        Lock secondLock = lockAcquirementOrder == 0 ? teclado : ordenador;
        return cogerDos(firstLock, secondLock);
    }

    /**
     * Intenta coger dos cerrojos, uno tras otro, insistiendo ESPERA_CERROJO_MS por cada uno.
     * Si el segundo no llega, suelta el primero: nunca espera sin límite con uno en la mano.
     *
     * @return true si tiene los dos; false si no tiene ninguno
     */
    private boolean cogerDos(Lock primero, Lock segundo) throws InterruptedException {
        if (!primero.tryLock(ESPERA_CERROJO_MS, TimeUnit.MILLISECONDS)) {
            return false;
        }
        try {
            if (!segundo.tryLock(ESPERA_CERROJO_MS, TimeUnit.MILLISECONDS)) {
                primero.unlock();
                return false;
            }
        } catch (InterruptedException e) {
            primero.unlock();
            throw e;
        }
        return true;
    }

    private void soltarOrdenadorYTeclado() {
        if (teclado != null) {
            teclado.unlock();
            teclado = null;
        }
        if (ordenador != null) {
            ordenador.unlock();
            ordenador = null;
        }
    }

    /** Una espera al azar antes de volver a intentarlo: así dos filósofos no repiten el choque. */
    private void esperaAlAzar() throws InterruptedException {
        Thread.sleep(ESPERA_MINIMA_MS + azar.nextInt(ESPERA_MAXIMA_MS - ESPERA_MINIMA_MS));
    }

    /**
     * Una visita al teatro: hasta ENTRADAS_POR_VISITA compras en una conexión.
     *
     * @return true si le han atendido (aunque estuvieran agotadas); false si el teatro no
     *         le ha admitido (OCUPADO) o no ha podido conectar
     */
    private boolean visitarElTeatro() {
        // TAREA 6. Dentro de un try con recursos, abre un Socket a (maquina, puerto) y sobre
        // él un DataInputStream `delTeatro` y un DataOutputStream `alTeatro`.
        //   (a) Lee el saludo. Si no es BIENVENIDO, avisa por consola y devuelve false.
        //       Si lo es, lee el número de la taquilla que te atiende y dilo por consola.
        //   (b) Hasta ENTRADAS_POR_VISITA veces, mientras tengas menos de ENTRADAS_DESEADAS
        //       y no estén agotadas: escribe COMPRAR y tu número, vacía el flujo y lee la
        //       respuesta. ENTRADA: lee la butaca y añádela a `entradas`. AGOTADAS: pon
        //       `agotadas` a true.
        //   (c) Escribe ADIOS, vacía el flujo, pon fallosConexion a 0 y devuelve true.
        //   Si salta una IOException (el teatro no está, o ha cerrado): fallosConexion++,
        //   avisa por consola y devuelve false.
        try (
                Socket socket = new Socket(maquina, puerto);
                DataInputStream fromTheater = new DataInputStream(socket.getInputStream());
                DataOutputStream toTheater = new DataOutputStream(socket.getOutputStream());
        ) {
            // Lectura handshake
            int codOpHandshake = fromTheater.readInt();
            if (codOpHandshake != Protocolo.BIENVENIDO) {
                di("No se pudo establecer conexión con el servidor");
                return false;
            }
            int numTaquilla = fromTheater.readInt();
            di(String.format("Me está atendiendo la taquilla %d", numTaquilla));
            // Inicio proceso compra entradas
            int ticketsBought = 0;
            while (!agotadas &&
                    ticketsBought < Protocolo.ENTRADAS_POR_VISITA &&
                    entradas.size() < Protocolo.ENTRADAS_DESEADAS
            ) {
                // Inicio orden de compra
                toTheater.writeInt(Protocolo.COMPRAR);
                toTheater.writeInt(numero);
                toTheater.flush();
                // Fin orden de compra
                // Lectura de respuesta
                int codOpAnswer = fromTheater.readInt();
                if (codOpAnswer == Protocolo.ENTRADA) {
                    int butaca = fromTheater.readInt();
                    entradas.add(butaca);
                    ticketsBought++;
                } else if (codOpAnswer == Protocolo.AGOTADAS) {
                    agotadas = true;
                } else {
                    di("Cod. Op. desconocido (cerrando conexión)");
                    return false;
                }
            }
            // Fin proceso compra entradas
            // Inicio proceso de desconexión
            toTheater.writeInt(Protocolo.ADIOS);
            toTheater.flush();
            // Fin proceso de desconexión
            fallosConexion = 0;
            return true;
        } catch (IOException ex) {
            fallosConexion++;
            di("El teatro no se encuentra operativo");
            return false;
        }
    }

    private void di(String que) {
        Registro.linea("filósofo " + numero, que);
    }
}
