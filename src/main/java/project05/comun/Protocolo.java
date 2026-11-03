package project05.comun;

/**
 * El protocolo del teatro: los códigos de operación y la carga de cada mensaje.
 *
 * Todo viaja con DataInputStream / DataOutputStream, en el orden de esta tabla.
 * El servidor habla primero: nada más aceptar la conexión dice BIENVENIDO o OCUPADO.
 *
 *   Sentido              Código       Carga tras el código   Qué significa
 *   servidor -> cliente  BIENVENIDO   int taquilla           le atiende la taquilla número n
 *   servidor -> cliente  OCUPADO      (ninguna)              las dos taquillas están ocupadas;
 *                                                            el servidor cierra a continuación
 *   cliente -> servidor  COMPRAR      int filosofo           una entrada, por favor
 *   servidor -> cliente  ENTRADA      int butaca             aquí tiene la entrada
 *   servidor -> cliente  AGOTADAS     (ninguna)              no quedan entradas
 *   cliente -> servidor  ADIOS        (ninguna)              el cliente cierra a continuación
 *
 * Esta clase la comparten cliente y servidor: es la manera más barata de que los dos
 * extremos respeten el contrato (como BankProtocol en la práctica 4).
 */
public final class Protocolo {

    public static final String MAQUINA = "localhost";
    public static final int PUERTO = 9876;

    // Servidor -> cliente
    public static final int BIENVENIDO = 1;   // + int taquilla
    public static final int OCUPADO = 2;      // sin carga; el servidor cierra después
    public static final int ENTRADA = 3;      // + int butaca
    public static final int AGOTADAS = 4;     // sin carga

    // Cliente -> servidor
    public static final int COMPRAR = 10;     // + int filosofo
    public static final int ADIOS = 11;       // sin carga; el cliente cierra después

    // Las reglas del teatro y de los filósofos
    public static final int BUTACAS = 50;              // numeradas del 0 al 49
    public static final int TAQUILLAS = 2;             // clientes atendidos a la vez
    public static final int ENTRADAS_POR_VISITA = 10;  // compras por conexión
    public static final int ENTRADAS_DESEADAS = 20;    // las que quiere cada filósofo
    public static final int TIEMPO_VENTA_MS = 1000;    // lo que tarda cada venta
    public static final int PLAZO_SILENCIO_MS = 10_000; // silencio que tolera una taquilla

    private Protocolo() {
    }
}
