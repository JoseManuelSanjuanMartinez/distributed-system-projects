package project07.orb2;

/**
 * Práctica 7 · Parte 2 · El protocolo entre Agenda_proxy y Server, con objId.
 *
 * Como en la parte 1, una conexión TCP por llamada, y toda respuesta empieza
 * por un boolean. Lo nuevo es el objId: la respuesta a «crear» lo trae, y las
 * peticiones «escribir» y «leer» lo llevan detrás del código de operación.
 *
 *   crear      petición:  int 1
 *              respuesta: boolean y, si es true, int objId
 *   escribir   petición:  int 2, int objId, UTF clave, int valor
 *              respuesta: boolean
 *   leer       petición:  int 3, int objId, UTF clave
 *              respuesta: boolean y, si es true, int valor
 */
public final class Protocolo {

    public static final int PUERTO = 9998;

    public static final int CREAR = 1;
    public static final int ESCRIBIR = 2;
    public static final int LEER = 3;

    private Protocolo() { }
}
