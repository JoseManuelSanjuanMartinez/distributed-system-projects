package project05.cliente;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Los ordenadores y los teclados que comparten los filósofos: cada uno es un cerrojo.
 *
 * Un cerrojo explícito (Lock) permite lo que synchronized no permite: intentar cogerlo
 * durante un tiempo limitado (tryLock) y, si no se consigue, hacer otra cosa.
 */
public class Recursos {

    private final Lock[] ordenadores;
    private final Lock[] teclados;

    public Recursos(int numOrdenadores, int numTeclados) {
        ordenadores = new Lock[numOrdenadores];
        teclados = new Lock[numTeclados];
        for (int i = 0; i < numOrdenadores; i++) {
            ordenadores[i] = new ReentrantLock();
        }
        for (int i = 0; i < numTeclados; i++) {
            teclados[i] = new ReentrantLock();
        }
    }

    public int numOrdenadores() {
        return ordenadores.length;
    }

    public int numTeclados() {
        return teclados.length;
    }

    public Lock ordenador(int i) {
        return ordenadores[i];
    }

    public Lock teclado(int i) {
        return teclados[i];
    }
}
