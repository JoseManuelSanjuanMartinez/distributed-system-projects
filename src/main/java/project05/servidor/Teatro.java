package project05.servidor;

import project05.comun.Protocolo;
import project05.comun.Registro;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * El teatro: un servidor TCP con dos taquillas y cincuenta butacas.
 *
 * El bucle de accept() es el de la práctica 3, con una diferencia: antes de arrancar un hilo
 * para el cliente, el teatro comprueba que le queda una taquilla libre. Si no, le dice
 * OCUPADO y cierra: la conexión TCP se ha establecido (accept() no puede negarse), pero el
 * servicio no le admite.
 *
 * Cierra cuando ha vendido todas las butacas, no queda nadie en las taquillas y lleva
 * GRACIA_MS sin recibir a nadie: así cualquier filósofo que vuelva a intentarlo recibe
 * AGOTADAS y puede terminar.
 *
 * Uso: java servidor.Teatro [--rapido] [puerto]
 *   --rapido   las ventas no tardan (para pruebas de carga)
 *   puerto     un puerto distinto de Protocolo.PUERTO (para tener varios teatros)
 */
public class Teatro {

    public static final int GRACIA_MS = 5000;
    private static final int ESPERA_ACCEPT_MS = 1000;

    public static void main(String[] args) throws IOException {
        int tiempoVentaMs = Protocolo.TIEMPO_VENTA_MS;
        int puerto = Protocolo.PUERTO;
        for (String arg : args) {
            if (arg.equals("--rapido")) {
                tiempoVentaMs = 0;
            } else {
                puerto = Integer.parseInt(arg);
            }
        }
        new Teatro().abrir(puerto, tiempoVentaMs);
    }

    private final Butacas butacas = new Butacas(Protocolo.BUTACAS);
    private final Taquillas taquillas = new Taquillas(Protocolo.TAQUILLAS);
    private int visitasAtendidas = 0;
    private int visitasRechazadas = 0;

    public void abrir(int puerto, int tiempoVentaMs) throws IOException {
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            servidor.setSoTimeout(ESPERA_ACCEPT_MS);   // accept() despierta cada segundo
            di("abierto en el puerto " + puerto + " con " + Protocolo.TAQUILLAS + " taquillas y "
                    + butacas.libres() + " butacas");
            long ultimaVisita = System.currentTimeMillis();

            while (!haTerminado(ultimaVisita)) {
                Socket socket;
                try {
                    socket = servidor.accept();
                } catch (SocketTimeoutException e) {
                    continue;                         // nadie ha llamado: vuelve a mirar
                }
                ultimaVisita = System.currentTimeMillis();

                // TAREA 3. Pide una taquilla a `taquillas`. Si no queda ninguna (0), llama a
                // rechazar(socket). Si queda, cuenta la visita (visitasAtendidas++) y arranca
                // un hilo nuevo con un objeto Taquilla para este socket, con el número de la
                // taquilla, `butacas`, `taquillas` y tiempoVentaMs. Como en la práctica 3, el
                // hilo principal vuelve enseguida a accept(). (Sustituye la línea rechazar(socket)
                // de abajo, que está solo para que el esqueleto compile.)
                int taquilla = taquillas.tomar();
                if (taquilla == 0) {
                    rechazar(socket);
                }
                visitasAtendidas++;
                new Thread(new Taquilla(socket, taquilla, butacas, taquillas, tiempoVentaMs)).start();
            }
        }
        di("cerrado: " + butacas.vendidas() + " butacas vendidas, " + visitasAtendidas
                + " visitas atendidas, " + visitasRechazadas + " rechazadas; como máximo "
                + taquillas.maximoAbiertas() + " taquillas abiertas a la vez");
        di("butaca:filósofo  " + butacas.resumen());
    }

    /** Todo vendido, nadie en las taquillas y un rato sin visitas: el teatro cierra. */
    private boolean haTerminado(long ultimaVisita) {
        return butacas.agotadas() && taquillas.abiertas() == 0
                && System.currentTimeMillis() - ultimaVisita > GRACIA_MS;
    }

    /** Las dos taquillas están ocupadas: se lo decimos al cliente y cerramos su conexión. */
    private void rechazar(Socket socket) {
        visitasRechazadas++;
        try (Socket s = socket;
             DataOutputStream salida = new DataOutputStream(s.getOutputStream())) {
            s.setTcpNoDelay(true);
            salida.writeInt(Protocolo.OCUPADO);
            salida.flush();
            di("taquillas ocupadas: rechazo a " + s.getRemoteSocketAddress());
        } catch (IOException e) {
            di("no he podido rechazar: " + e.getMessage());
        }
    }

    private void di(String que) {
        Registro.linea("teatro", que);
    }
}
