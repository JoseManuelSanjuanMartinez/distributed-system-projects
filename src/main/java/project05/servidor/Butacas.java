package project05.servidor;

import java.util.Arrays;

/**
 * El patio de butacas del teatro: cada butaca se vende una sola vez.
 *
 * Es el estado compartido del servidor: las dos taquillas (dos hilos) venden sobre este
 * mismo objeto, así que sus métodos tienen que ser atómicos, como el contador de la
 * práctica 1 y las cuentas de la práctica 4.
 */
public class Butacas {

    /** comprador[i] es el número del filósofo que compró la butaca i, o -1 si está libre. */
    private final int[] comprador;

    public Butacas(int cuantas) {
        comprador = new int[cuantas];
        Arrays.fill(comprador, -1);
    }

    /**
     * Vende la primera butaca libre al filósofo dado.
     *
     * @return el número de la butaca vendida, o -1 si no queda ninguna
     */
    public synchronized int vender(int filosofo) {
        // TAREA 1. Busca la primera butaca libre (comprador[i] == -1), apúntala como
        // vendida a este filósofo y devuelve su número; si no hay ninguna, devuelve -1.
        // Dos taquillas venden a la vez sobre este mismo objeto: haz el método seguro
        // frente a hilos, como el contador de la práctica 1.
        int i = 0;
        boolean found = false;
        while (i < comprador.length && !found) {
            if (comprador[i] == -1) {
                comprador[i] = filosofo;
                found = true;
            } else {i++;}
        }
        return found ? i : -1;
    }

    public synchronized int vendidas() {
        int n = 0;
        for (int c : comprador) {
            if (c != -1) {
                n++;
            }
        }
        return n;
    }

    public synchronized int libres() {
        return comprador.length - vendidas();
    }

    public synchronized boolean agotadas() {
        return libres() == 0;
    }

    /** Quién compró cada butaca: «0:3 1:3 2:1 …» (butaca:filósofo). */
    public synchronized String resumen() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < comprador.length; i++) {
            if (comprador[i] != -1) {
                sb.append(i).append(':').append(comprador[i]).append(' ');
            }
        }
        return sb.toString().trim();
    }

    /** Un paso lento entre buscar la butaca y apuntarla: hace visible la carrera (pregunta 3). */
    static void pausa(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
