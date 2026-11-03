package project06.calculadora;

/**
 * Práctica 6 · Lo que una llamada local nunca hace: fallar por la red.
 *
 * El proxy la lanza cuando no puede conectar, cuando se corta la conexión o
 * cuando la respuesta no llega a tiempo. En Java RMI su equivalente es
 * java.rmi.RemoteException, y allí es obligatorio declararla en cada método
 * remoto (véase la pregunta P3).
 */
public class LlamadaRemotaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LlamadaRemotaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
