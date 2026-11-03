package project05.servidor;

import project05.comun.Protocolo;
import project05.comun.Registro;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Una taquilla: el hilo que atiende a un cliente durante toda su conexión.
 *
 * Es el ServicioEco de la práctica 3 con el protocolo de la práctica 4 dentro: saluda,
 * y después lee un código de operación tras otro y despacha con un switch, hasta que el
 * cliente se despide (ADIOS), cierra sin despedirse (EOFException), se queda callado
 * demasiado tiempo (SocketTimeoutException) o envía algo que no entendemos.
 */
public class Taquilla implements Runnable {

    private final Socket socket;
    private final int numero;
    private final Butacas butacas;
    private final Taquillas taquillas;
    private final int tiempoVentaMs;

    public Taquilla(Socket socket, int numero, Butacas butacas, Taquillas taquillas,
                    int tiempoVentaMs) {
        this.socket = socket;
        this.numero = numero;
        this.butacas = butacas;
        this.taquillas = taquillas;
        this.tiempoVentaMs = tiempoVentaMs;
    }

    @Override
    public void run() {
        String cliente = String.valueOf(socket.getRemoteSocketAddress());
        try (Socket s = socket;
             DataInputStream entrada = new DataInputStream(s.getInputStream());
             DataOutputStream salida = new DataOutputStream(s.getOutputStream())) {

            s.setSoTimeout(Protocolo.PLAZO_SILENCIO_MS);
            s.setTcpNoDelay(true);                  // mensajes pequeños: se envían al momento
            di("abro para " + cliente);

            // TAREA 4 (a). Saluda: escribe BIENVENIDO y el número de esta taquilla, y vacía
            // el flujo (flush).
            // TAREA 4 (b). Mientras el cliente siga: lee un código de operación (readInt).
            //   COMPRAR: lee el número del filósofo y llama a vender(filosofo, salida).
            //   ADIOS: el cliente se despide; sal del bucle.
            //   Cualquier otro código: avisa por consola y sal del bucle (¿por qué no se
            //   puede ignorar y leer el siguiente? Lo viste en la práctica 4).
            // El fin de flujo, el plazo de espera y los errores ya los tratan los catch de
            // abajo, y el finally libera la taquilla: no hace falta que hagas nada ahí.

            // Inicio Handshake
            salida.writeInt(Protocolo.BIENVENIDO);
            salida.writeInt(numero);
            salida.flush();
            // Fin Handshake
            boolean endConn = false;
            do {
                int codOp = entrada.readInt();
                if (codOp == Protocolo.COMPRAR) {
                    int filosofo = entrada.readInt();
                    vender(filosofo, salida);
                } else if (codOp == Protocolo.ADIOS) {
                    endConn = true;
                } else {
                    di("Cod. Op. desconocido (cerrando conexión)");
                    endConn = true;
                }
            } while (!endConn);

        } catch (EOFException e) {
            // El cliente cerró (o murió) entre dos peticiones: en un flujo binario el fin
            // de flujo llega como una EOFException (práctica 4).
            di("el cliente ha cerrado sin despedirse");
        } catch (SocketTimeoutException e) {
            di("el cliente lleva " + Protocolo.PLAZO_SILENCIO_MS + " ms callado: cierro");
        } catch (IOException e) {
            di("error con " + cliente + ": " + e.getMessage());
        } finally {
            // Pase lo que pase, la taquilla vuelve a estar libre para el siguiente cliente.
            taquillas.liberar(numero);
            di("cierro");
        }
    }

    /** Una venta: tarda lo que tarda, y responde ENTRADA + butaca o AGOTADAS. */
    private void vender(int filosofo, DataOutputStream salida) throws IOException {
        pausa(tiempoVentaMs);
        int butaca = butacas.vender(filosofo);
        if (butaca >= 0) {
            salida.writeInt(Protocolo.ENTRADA);
            salida.writeInt(butaca);
            di("vendo la butaca " + butaca + " al filósofo " + filosofo
                    + " (quedan " + butacas.libres() + ")");
        } else {
            salida.writeInt(Protocolo.AGOTADAS);
            di("entradas agotadas para el filósofo " + filosofo);
        }
        salida.flush();                                   // sin vaciado automático (práctica 4)
    }

    private void pausa(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void di(String que) {
        Registro.linea("taquilla " + numero, que);
    }
}
