package project06.calculadora;

/**
 * Práctica 6 · El protocolo, compartido por el proxy y el skeleton.
 *
 * Una conexión TCP por llamada (como la plantilla rpc y como la práctica 7),
 * no por sesión (como el banco de la práctica 4).
 *
 *   Petición:  [long idLlamada, sólo si CON_ID]
 *              int opcode, y después sus argumentos, todos int
 *              (sumar, restar, multiplicar, dividir: dos; acumular: uno)
 *   Respuesta: byte estado, y después
 *                 OK     → int resultado
 *                 ERROR  → UTF mensaje
 */
public final class Protocolo {

    public static final int PUERTO = 5001;

    public static final int OP_SUMAR = 1;
    public static final int OP_RESTAR = 2;
    public static final int OP_MULTIPLICAR = 3;
    public static final int OP_DIVIDIR = 4;
    public static final int OP_ACUMULAR = 5;

    public static final byte OK = 0;
    public static final byte ERROR = 1;

    /**
     * Parte 4 (opcional): con true, cada petición empieza con un long que
     * identifica la llamada, el mismo en todos sus reintentos. Proxy y
     * skeleton tienen que estar de acuerdo: es parte del protocolo.
     */
    public static final boolean CON_ID = true;

    private Protocolo() { }
}
