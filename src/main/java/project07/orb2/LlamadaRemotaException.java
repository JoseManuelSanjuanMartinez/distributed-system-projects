package project07.orb2;

/**
 * Práctica 7 · Lo que una llamada local nunca hace: fallar por la red, o porque
 * el servidor no ha podido ejecutarla.
 *
 * El proxy la lanza cuando no puede conectar con el servidor, cuando se corta
 * la conexión y cuando la respuesta empieza por false. Hereda de
 * RuntimeException, como en la práctica 6, porque IRepository no declara
 * ninguna excepción.
 */
public class LlamadaRemotaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LlamadaRemotaException(String mensaje) {
        super(mensaje);
    }

    public LlamadaRemotaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
