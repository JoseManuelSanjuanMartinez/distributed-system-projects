package project06.calculadora;

/**
 * Práctica 6 · La implementación de verdad (el «sirviente»).
 *
 * Una clase Java normal, sin nada de red. En el servidor, el skeleton la
 * llama; en el cliente, la demo puede usarla directamente para comparar.
 */
public class CalculadoraLocal implements Calculadora {

    private int memoria = 0;

    @Override
    public int sumar(int a, int b) {
        return a + b;
    }

    @Override
    public int restar(int a, int b) {
        return a - b;
    }

    @Override
    public int multiplicar(int a, int b) {
        return a * b;
    }

    @Override
    public int dividir(int a, int b) {
        return a / b;            // con b == 0, Java lanza ArithmeticException
    }

    @Override
    public synchronized int acumular(int x) {
        memoria += x;            // varios hilos del servidor pueden llegar a la vez
        return memoria;
    }
}
