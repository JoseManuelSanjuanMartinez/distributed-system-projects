package project06.calculadora;

/**
 * Práctica 6 · El mismo programa, en local o en remoto.
 *
 * Uso: DemoCalculadora local
 *      DemoCalculadora remota [máquina]
 * Lo único que cambia es la línea que elige la implementación.
 */
public class DemoCalculadora {

    public static void main(String[] args) {
        boolean remota = args.length > 0 && args[0].equals("remota");
        String maquina = args.length > 1 ? args[1] : "localhost";

        Calculadora c = remota
                ? new CalculadoraProxy(maquina, Protocolo.PUERTO)
                : new CalculadoraLocal();
        System.out.println("Calculadora " + (remota ? "remota en " + maquina : "local"));

        System.out.println("sumar(7, 5)       = " + c.sumar(7, 5));
        System.out.println("restar(7, 5)      = " + c.restar(7, 5));
        System.out.println("multiplicar(7, 5) = " + c.multiplicar(7, 5));
        System.out.println("dividir(7, 5)     = " + c.dividir(7, 5));
        try {
            System.out.println("dividir(7, 0)     = " + c.dividir(7, 0));
        } catch (ArithmeticException e) {
            System.out.println("dividir(7, 0)     -> ArithmeticException: " + e.getMessage());
        }
    }
}
