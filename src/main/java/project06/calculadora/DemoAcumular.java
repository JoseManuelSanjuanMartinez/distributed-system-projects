package project06.calculadora;

/**
 * Práctica 6 · Parte 3: una operación con estado, llamada con reintentos.
 *
 * Uso (contra un servidor recién arrancado): DemoAcumular [máquina]
 * Hace cinco veces acumular(10) con un proxy que lo intenta hasta 3 veces.
 */
public class DemoAcumular {

    public static void main(String[] args) {
        String maquina = args.length > 0 ? args[0] : "localhost";
        Calculadora c = new CalculadoraProxy(maquina, Protocolo.PUERTO, 3);

        int total = 0;
        for (int i = 1; i <= 5; i++) {
            total = c.acumular(10);
            System.out.println("acumular(10), llamada " + i + " -> memoria = " + total);
        }
        System.out.println("Cinco llamadas de 10: esperado 50, obtenido " + total);
    }
}
