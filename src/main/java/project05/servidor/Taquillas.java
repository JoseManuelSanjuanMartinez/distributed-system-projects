package project05.servidor;

/**
 * Las taquillas del teatro: cuántas hay y cuáles están ocupadas.
 *
 * El hilo principal del servidor toma una taquilla por cada cliente que admite, y el hilo
 * de esa taquilla la libera al terminar: dos hilos distintos tocan el mismo estado, así que
 * los métodos tienen que ser atómicos.
 */
public class Taquillas {

    /** ocupada[t] dice si la taquilla t está atendiendo a alguien (el índice 0 no se usa). */
    private final boolean[] ocupada;

    /** Cuántas taquillas han estado abiertas a la vez, como máximo (comprueba el límite). */
    private int maximoAbiertas = 0;

    public Taquillas(int cuantas) {
        ocupada = new boolean[cuantas + 1];
    }

    /**
     * Toma una taquilla libre para un cliente nuevo.
     *
     * @return el número de la taquilla (1, 2, …), o 0 si todas están ocupadas
     */
    public synchronized int tomar() {
        // TAREA 2 (a). Busca una taquilla libre (índices 1 a ocupada.length - 1), márcala
        // como ocupada y devuelve su número; si no hay ninguna, devuelve 0. Actualiza maximoAbiertas
        // con Math.max(maximoAbiertas, abiertas()). El hilo principal toma, los hilos de
        // las taquillas liberan: los dos métodos tienen que ser atómicos.
        int i = 1; // <-- ATENCIÓN: el índice 0 no se usa
        boolean found = false;
        while (i < ocupada.length && !found) {
            if (!ocupada[i]) {
                ocupada[i] = true;
                found = true;
            } else  {
                i++;
            }
        }
        maximoAbiertas = Math.max(maximoAbiertas, abiertas());
        return found ? i : 0;
    }

    /** Devuelve la taquilla t al terminar de atender a un cliente. */
    public synchronized void liberar(int t) {
        // TAREA 2 (b). Marca la taquilla t como libre.
        ocupada[t] = false;
    }

    /** Cuántas taquillas están atendiendo a alguien ahora mismo. */
    public synchronized int abiertas() {
        int n = 0;
        for (boolean o : ocupada) {
            if (o) {
                n++;
            }
        }
        return n;
    }

    public synchronized int maximoAbiertas() {
        return maximoAbiertas;
    }
}
