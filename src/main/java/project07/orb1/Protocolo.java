package project07.orb1;

/**
 * Práctica 7 · Parte 1 · El protocolo entre Agenda_proxy y Server.
 *
 * Una conexión TCP por llamada: el proxy la abre, envía la petición, lee la
 * respuesta y la cierra. Toda respuesta empieza por un boolean, true si el
 * servidor ha podido hacer la operación.
 *
 *   crear      petición:  int 1
 *              respuesta: boolean
 *   escribir   petición:  int 2, UTF clave, int valor
 *              respuesta: boolean
 *   leer       petición:  int 3, UTF clave
 *              respuesta: boolean y, si es true, int valor
 */
public final class Protocolo {

    public static final int PUERTO = 9999;

    public static final int CREAR = 1;
    public static final int ESCRIBIR = 2;
    public static final int LEER = 3;

    private Protocolo() { }
}
