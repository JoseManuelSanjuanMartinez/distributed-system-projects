package project06.calculadora;

/**
 * Práctica 6 · El contrato del servicio.
 *
 * Es lo único que conocen a la vez el cliente y el servidor. El programa
 * cliente sólo ve esta interfaz: no sabe si detrás hay un objeto local
 * (CalculadoraLocal) o un proxy que habla por la red (CalculadoraProxy).
 */
public interface Calculadora {

    int sumar(int a, int b);

    int restar(int a, int b);

    int multiplicar(int a, int b);

    /** Lanza ArithmeticException si b == 0, en local y en remoto. */
    int dividir(int a, int b);

    /**
     * La memoria de la calculadora (la tecla M+): suma x a un total que
     * guarda el servidor y devuelve el total nuevo. Es la única operación
     * con estado, y la que importa en la parte 3.
     */
    int acumular(int x);
}
