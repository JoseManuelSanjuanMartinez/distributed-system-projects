package project05.cliente;

import project05.comun.Protocolo;
import project05.comun.Registro;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * El lado del cliente: cinco filósofos, tres ordenadores y tres teclados.
 *
 * Arranca los cinco hilos, espera a que terminen y hace el recuento: cuántas entradas ha
 * conseguido cada uno, cuántas en total, y si alguna butaca aparece dos veces.
 *
 * Uso: java cliente.Filosofos [puerto]
 */
public class Filosofos {

    public static final int FILOSOFOS = 5;
    public static final int ORDENADORES = 3;
    public static final int TECLADOS = 3;

    public static void main(String[] args) throws InterruptedException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : Protocolo.PUERTO;
        Recursos recursos = new Recursos(ORDENADORES, TECLADOS);

        List<Filosofo> filosofos = new ArrayList<>();
        List<Thread> hilos = new ArrayList<>();
        for (int n = 1; n <= FILOSOFOS; n++) {
            Filosofo f = new Filosofo(n, recursos, Protocolo.MAQUINA, puerto);
            filosofos.add(f);
            Thread hilo = new Thread(f, "filosofo-" + n);
            hilos.add(hilo);
            hilo.start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }

        // El recuento: todas las butacas distintas, y entre todos las que había
        List<Integer> todas = new ArrayList<>();
        for (Filosofo f : filosofos) {
            todas.addAll(f.entradas());
        }
        TreeSet<Integer> distintas = new TreeSet<>(todas);
        Registro.linea("recuento", todas.size() + " entradas en total, " + distintas.size()
                + " butacas distintas"
                + (todas.size() == distintas.size() ? "" : " (¡REPETIDAS!)"));
    }
}
